package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.DocumentDataDto;
import com.example.ServiceApp.dto.DocumentEquipmentDto;
import com.example.ServiceApp.entity.DocumentData;
import com.example.ServiceApp.entity.DocumentEquipment;
import com.example.ServiceApp.mapper.DocumentDataMapper;
import com.example.ServiceApp.repository.DocumentDataRepository;
import com.example.ServiceApp.repository.DocumentEquipmentRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentDataService {

    private final DocumentDataRepository documentDataRepository;
    private final DocumentEquipmentRepository documentEquipmentRepository;

    public DocumentDataService(DocumentDataRepository documentDataRepository,
                               DocumentEquipmentRepository documentEquipmentRepository) {
        this.documentDataRepository = documentDataRepository;
        this.documentEquipmentRepository = documentEquipmentRepository;
    }

    @Transactional
    public DocumentDataDto create(DocumentDataDto dto) {
        if (dto.getId() != null) {
            return update(dto.getId(), dto);
        }

        DocumentData entity = DocumentDataMapper.toEntity(dto);

        // Save document first without equipments to get the ID
        List<DocumentEquipment> equipments = entity.getEquipments();
        entity.setEquipments(new ArrayList<>());
        DocumentData saved = documentDataRepository.save(entity);

        // Save equipments directly through repository
        if (equipments != null && !equipments.isEmpty()) {
            for (int i = 0; i < equipments.size(); i++) {
                DocumentEquipment eq = equipments.get(i);
                eq.setDocumentDataId(saved.getId());
                eq.setSortOrder(i);
            }
            documentEquipmentRepository.saveAll(equipments);
        }

        // Reload to get equipments in the response
        return DocumentDataMapper.toDto(documentDataRepository.findById(saved.getId()).orElse(saved));
    }

    public List<DocumentDataDto> getAll() {
        Pageable topFifty = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "contractDate"));
        Page<DocumentData> list = documentDataRepository.findAllByOrderByContractDateDesc(topFifty);
        return DocumentDataMapper.toDtoList(list.getContent());
    }

    public DocumentDataDto getById(Long id) {
        Optional<DocumentData> optional = documentDataRepository.findById(id);
        return optional.map(DocumentDataMapper::toDto).orElse(null);
    }

    @Transactional
    public DocumentDataDto update(Long id, DocumentDataDto dto) {
        Optional<DocumentData> optional = documentDataRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        DocumentData existing = optional.get();

        existing.setCustomerId(dto.getCustomerId());
        existing.setCui(dto.getCui());
        existing.setContractDate(dto.getContractDate());
        existing.setMonthOfWarranty(dto.getMonthOfWarranty());
        existing.setMonthOfWarrantyHandPieces(dto.getMonthOfWarrantyHandPieces());
        existing.setNumberOfContract(dto.getNumberOfContract());

        // Update equipments: keep existing, add new, remove deleted
        // Get current equipment IDs directly from database (not from collection)
        List<DocumentEquipment> currentEquipments = documentEquipmentRepository.findByDocumentDataIdOrderBySortOrderAsc(existing.getId());
        Set<Long> existingEquipmentIds = currentEquipments.stream()
                .map(DocumentEquipment::getId)
                .collect(Collectors.toSet());

        if (dto.getEquipments() != null && !dto.getEquipments().isEmpty()) {
            // Collect IDs of equipments from DTO (existing ones have IDs)
            Set<Long> dtoEquipmentIds = dto.getEquipments().stream()
                    .map(DocumentEquipmentDto::getId)
                    .filter(eqId -> eqId != null)
                    .collect(Collectors.toSet());

            // Find IDs to delete (in DB but not in DTO)
            List<Long> idsToDelete = existingEquipmentIds.stream()
                    .filter(eqId -> !dtoEquipmentIds.contains(eqId))
                    .collect(Collectors.toList());

            // Delete removed equipments directly by IDs
            if (!idsToDelete.isEmpty()) {
                documentEquipmentRepository.deleteAllByIdIn(idsToDelete);
            }

            // Update existing equipments directly through repository
            List<DocumentEquipment> toSave = new ArrayList<>();

            for (int i = 0; i < dto.getEquipments().size(); i++) {
                DocumentEquipmentDto eqDto = dto.getEquipments().get(i);

                DocumentEquipment eq = DocumentEquipment.builder()
                        .id(eqDto.getId())  // null for new, existing ID for update
                        .documentDataId(existing.getId())
                        .equipmentId(eqDto.getEquipmentId())
                        .equipmentName(eqDto.getEquipmentName())
                        .productCode(eqDto.getProductCode())
                        .serialNumber(eqDto.getSerialNumber())
                        .sortOrder(i)
                        .build();
                toSave.add(eq);
            }

            // Save all equipments (updates and new)
            if (!toSave.isEmpty()) {
                documentEquipmentRepository.saveAll(toSave);
            }

        } else {
            // Delete all equipments if list is empty or null
            if (!existingEquipmentIds.isEmpty()) {
                documentEquipmentRepository.deleteAllByIdIn(new ArrayList<>(existingEquipmentIds));
            }
        }

        existing.setSignatureDate(dto.getSignatureDate());
        existing.setTrainedPerson(dto.getTrainedPerson());
        existing.setJobFunction(dto.getJobFunction());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setContactPerson(dto.getContactPerson());

        DocumentData updated = documentDataRepository.save(existing);
        return DocumentDataMapper.toDto(updated);
    }

    public boolean delete(Long id) {
        if (!documentDataRepository.existsById(id)) {
            return false;
        }
        documentDataRepository.deleteById(id);
        return true;
    }

    public List<DocumentDataDto> search(String keyword) {
        List<DocumentData> documentDataList = documentDataRepository.searchDocument(keyword);
        return DocumentDataMapper.toDtoList(documentDataList);
    }

    public ByteArrayOutputStream generateDocument(DocumentDataDto data, String type) throws Exception {
        String templateFile = switch (type.toLowerCase()) {
            case "predare" -> "pv_predare_primire.docx";
            case "garantie" -> "certificat_garantie.docx";
            case "punere" -> "pv_punere_in_functiune.docx";
            case "receptie" -> "pv_receptie.docx";
            default -> throw new IllegalArgumentException("Tip necunoscut: " + type);
        };

        // Debug logging
        System.out.println("=== GENERATE DOCUMENT DEBUG ===");
        System.out.println("CustomerName: " + data.getCustomerName());
        System.out.println("CUI: " + data.getCui());
        System.out.println("ContractDate: " + data.getContractDate());
        System.out.println("Equipments count: " + (data.getEquipments() != null ? data.getEquipments().size() : 0));
        if (data.getEquipments() != null) {
            for (int i = 0; i < data.getEquipments().size(); i++) {
                DocumentEquipmentDto eq = data.getEquipments().get(i);
                System.out.println("  Equipment " + i + ": name=" + eq.getEquipmentName() + ", code=" + eq.getProductCode() + ", serial=" + eq.getSerialNumber());
            }
        }
        System.out.println("================================");

        try (InputStream templateStream = new ClassPathResource("templates/" + templateFile).getInputStream();
             XWPFDocument document = new XWPFDocument(templateStream)) {

            // Replace placeholders in text
            replacePlaceholders(document, buildValueMap(data));

            // Generate dynamic equipment rows in tables
            generateEquipmentRows(document, data.getEquipments());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);

            return out;
        }
    }

    private void generateEquipmentRows(XWPFDocument document, List<DocumentEquipmentDto> equipments) {
        if (equipments == null || equipments.isEmpty()) {
            return;
        }

        for (XWPFTable table : document.getTables()) {
            int templateRowIndex = findTemplateRow(table);
            if (templateRowIndex == -1) {
                continue;
            }

            XWPFTableRow templateRow = table.getRow(templateRowIndex);

            // First, clone the template row for all additional equipments (before modifying the template)
            List<XWPFTableRow> allRows = new ArrayList<>();
            allRows.add(templateRow);  // First row is the template itself

            for (int i = 1; i < equipments.size(); i++) {
                // Clone template row BEFORE any modifications
                XWPFTableRow clonedRow = cloneRow(table, templateRow, templateRowIndex + i);
                allRows.add(clonedRow);
            }

            // Now replace placeholders in each row with corresponding equipment data
            for (int i = 0; i < equipments.size(); i++) {
                DocumentEquipmentDto eq = equipments.get(i);
                replaceInRow(allRows.get(i), eq, i + 1);
            }
        }
    }

    private int findTemplateRow(XWPFTable table) {
        for (int i = 0; i < table.getRows().size(); i++) {
            XWPFTableRow row = table.getRow(i);
            for (XWPFTableCell cell : row.getTableCells()) {
                String text = cell.getText();
                if (text != null && (text.contains("${nameOfEquipment}") ||
                        text.contains("${equipmentName}") ||
                        text.contains("${productCode}") ||
                        text.contains("${serialNumber}"))) {
                    return i;
                }
            }
        }
        return -1;
    }

    private XWPFTableRow cloneRow(XWPFTable table, XWPFTableRow sourceRow, int insertPosition) {
        // Create new row in the table at the specified position
        XWPFTableRow newRow = table.insertNewTableRow(insertPosition);

        // Copy cells from source row
        List<XWPFTableCell> sourceCells = sourceRow.getTableCells();
        for (int i = 0; i < sourceCells.size(); i++) {
            XWPFTableCell sourceCell = sourceCells.get(i);
            XWPFTableCell newCell;

            if (i == 0) {
                newCell = newRow.getCell(0);
                if (newCell == null) {
                    newCell = newRow.createCell();
                }
            } else {
                newCell = newRow.createCell();
            }

            // Copy text content from each paragraph
            List<XWPFParagraph> sourceParagraphs = sourceCell.getParagraphs();
            if (sourceParagraphs != null && !sourceParagraphs.isEmpty()) {
                // Remove default paragraph if exists
                while (newCell.getParagraphs().size() > 0) {
                    newCell.removeParagraph(0);
                }

                for (XWPFParagraph sourcePara : sourceParagraphs) {
                    XWPFParagraph newPara = newCell.addParagraph();
                    // Copy paragraph text with all runs
                    for (XWPFRun sourceRun : sourcePara.getRuns()) {
                        XWPFRun newRun = newPara.createRun();
                        newRun.setText(sourceRun.getText(0));
                        // Copy basic formatting
                        newRun.setBold(sourceRun.isBold());
                        newRun.setItalic(sourceRun.isItalic());
                        if (sourceRun.getFontSizeAsDouble() != null) {
                            newRun.setFontSize(sourceRun.getFontSizeAsDouble());
                        }
                        newRun.setFontFamily(sourceRun.getFontFamily());
                    }
                }
            }
        }

        return newRow;
    }

    private void replaceInRow(XWPFTableRow row, DocumentEquipmentDto eq, int index) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("${nameOfEquipment}", eq.getEquipmentName() != null ? eq.getEquipmentName() : "");
        replacements.put("${equipmentName}", eq.getEquipmentName() != null ? eq.getEquipmentName() : "");
        replacements.put("${productCode}", eq.getProductCode() != null ? eq.getProductCode() : "");
        replacements.put("${serialNumber}", eq.getSerialNumber() != null ? eq.getSerialNumber() : "");
        replacements.put("${nr}", String.valueOf(index));

        // Get cells - try both methods to ensure we get all cells
        List<XWPFTableCell> cells = row.getTableCells();
        if (cells == null || cells.isEmpty()) {
            // Fallback: manually iterate through CTRow cells
            for (int i = 0; i < row.getCtRow().sizeOfTcArray(); i++) {
                XWPFTableCell cell = row.getCell(i);
                if (cell != null) {
                    replaceCellContent(cell, replacements);
                }
            }
        } else {
            for (XWPFTableCell cell : cells) {
                replaceCellContent(cell, replacements);
            }
        }
    }

    private void replaceCellContent(XWPFTableCell cell, Map<String, String> replacements) {
        // Try getting paragraphs normally first
        List<XWPFParagraph> paragraphs = cell.getParagraphs();
        if (paragraphs != null && !paragraphs.isEmpty()) {
            for (XWPFParagraph paragraph : paragraphs) {
                replaceInParagraph(paragraph, replacements);
            }
        } else {
            // Fallback: directly replace in cell text
            String cellText = cell.getText();
            if (cellText != null) {
                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                    if (cellText.contains(entry.getKey())) {
                        cellText = cellText.replace(entry.getKey(), entry.getValue());
                    }
                }
                // Clear and set new text
                cell.removeParagraph(0);
                cell.setText(cellText);
            }
        }
    }

    private void replacePlaceholders(XWPFDocument document, Map<String, String> values) {
        Map<String, String> placeholders = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : values.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            if (k == null) continue;
            if (k.startsWith("${") && k.endsWith("}")) {
                placeholders.put(k, v);
                String bare = k.substring(2, k.length() - 1);
                placeholders.putIfAbsent(bare, v);
            } else {
                placeholders.put("${" + k + "}", v);
                placeholders.putIfAbsent(k, v);
            }
        }

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceInParagraph(paragraph, placeholders);
        }

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceInParagraph(paragraph, placeholders);
                    }
                }
            }
        }

        try {
            for (XWPFHeader header : document.getHeaderList()) {
                for (XWPFParagraph p : header.getParagraphs()) replaceInParagraph(p, placeholders);
            }
            for (XWPFFooter footer : document.getFooterList()) {
                for (XWPFParagraph p : footer.getParagraphs()) replaceInParagraph(p, placeholders);
            }
        } catch (UnsupportedOperationException ignored) {
        }
    }

    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> placeholders) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        List<String> runTexts = new ArrayList<>(runs.size());
        for (XWPFRun r : runs) {
            String t = r.getText(0);
            runTexts.add(t == null ? "" : t);
        }

        StringBuilder concat = new StringBuilder();
        for (String s : runTexts) concat.append(s);
        String fullText = concat.toString();
        if (fullText.isEmpty()) return;

        while (true) {
            int foundPos = -1;
            String foundKey = null;
            String foundValue = null;

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String key = entry.getKey();
                if (key == null || key.isEmpty()) continue;
                int idx = fullText.indexOf(key);
                if (idx >= 0 && (foundPos == -1 || idx < foundPos)) {
                    foundPos = idx;
                    foundKey = key;
                    foundValue = entry.getValue() != null ? entry.getValue() : "";
                }
            }

            if (foundPos == -1) break;

            int startIndex = foundPos;
            int endIndex = startIndex + foundKey.length();

            int runStart = -1, runEnd = -1;
            int offsetInRunStart = -1, offsetInRunEnd = -1;
            int running = 0;
            for (int i = 0; i < runTexts.size(); i++) {
                int len = runTexts.get(i).length();
                if (runStart == -1 && startIndex >= running && startIndex < running + len) {
                    runStart = i;
                    offsetInRunStart = startIndex - running;
                }
                if (runEnd == -1 && endIndex > running && endIndex <= running + len) {
                    runEnd = i;
                    offsetInRunEnd = endIndex - running;
                }
                running += len;
                if (runStart != -1 && runEnd != -1) break;
            }

            if (runStart == -1) break;
            if (runEnd == -1) {
                runEnd = runTexts.size() - 1;
                offsetInRunEnd = runTexts.get(runEnd).length();
            }

            String startRunText = runTexts.get(runStart);
            String endRunText = runTexts.get(runEnd);

            String prefix = startRunText.substring(0, offsetInRunStart);
            String suffix = endRunText.substring(offsetInRunEnd);

            String newRunText = prefix + foundValue + suffix;

            XWPFRun targetRun = runs.get(runStart);
            setRunText(targetRun, newRunText);

            for (int rem = runEnd; rem > runStart; rem--) {
                paragraph.removeRun(rem);
            }

            runs = paragraph.getRuns();
            runTexts.clear();
            for (XWPFRun r : runs) {
                String t = r.getText(0);
                runTexts.add(t == null ? "" : t);
            }
            concat.setLength(0);
            for (String s : runTexts) concat.append(s);
            fullText = concat.toString();
        }
    }

    private void setRunText(XWPFRun run, String text) {
        int textCount = run.getCTR().sizeOfTArray();
        for (int i = textCount - 1; i >= 0; i--) {
            run.getCTR().removeT(i);
        }
        if (text != null && !text.isEmpty()) {
            run.setText(text, 0);
        } else {
            run.setText("", 0);
        }
    }

    private Map<String, String> buildValueMap(DocumentDataDto data) {
        Map<String, String> map = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        map.put("customerName", data.getCustomerName() != null ? data.getCustomerName() : "");
        map.put("cui", data.getCui() != null ? data.getCui() : "");
        map.put("contractDate", data.getContractDate() != null ? data.getContractDate().format(formatter) : "");
        map.put("monthOfWarranty", data.getMonthOfWarranty() != null ? String.valueOf(data.getMonthOfWarranty()) : "");
        map.put("monthOfWarrantyHandPieces", data.getMonthOfWarrantyHandPieces() != null ? String.valueOf(data.getMonthOfWarrantyHandPieces()) : "");
        map.put("numberOfContract", data.getNumberOfContract() != null ? data.getNumberOfContract() : "");

        map.put("signatureDate", data.getSignatureDate() != null ? data.getSignatureDate().format(formatter) : "");
        map.put("trainedPerson", data.getTrainedPerson() != null ? data.getTrainedPerson() : "");
        map.put("function", data.getJobFunction() != null ? data.getJobFunction() : "");
        map.put("phone", data.getPhone() != null ? data.getPhone() : "");
        map.put("contactPerson", data.getContactPerson() != null ? data.getContactPerson() : "");

        return map;
    }
}
