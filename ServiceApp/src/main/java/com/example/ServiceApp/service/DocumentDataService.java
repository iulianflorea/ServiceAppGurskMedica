package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.DocumentDataDto;
import com.example.ServiceApp.dto.DocumentEquipmentDto;
import com.example.ServiceApp.dto.DocumentProductDto;
import com.example.ServiceApp.dto.DocumentTrainedPersonDto;
import com.example.ServiceApp.entity.DocumentData;
import com.example.ServiceApp.entity.DocumentEquipment;
import com.example.ServiceApp.entity.DocumentProduct;
import com.example.ServiceApp.entity.DocumentTrainedPerson;
import com.example.ServiceApp.mapper.DocumentDataMapper;
import com.example.ServiceApp.repository.DocumentDataRepository;
import com.example.ServiceApp.repository.DocumentEquipmentRepository;
import com.example.ServiceApp.repository.DocumentProductRepository;
import com.example.ServiceApp.repository.DocumentTrainedPersonRepository;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
    private final DocumentProductRepository documentProductRepository;
    private final DocumentTrainedPersonRepository documentTrainedPersonRepository;

    public DocumentDataService(DocumentDataRepository documentDataRepository,
                               DocumentEquipmentRepository documentEquipmentRepository,
                               DocumentProductRepository documentProductRepository,
                               DocumentTrainedPersonRepository documentTrainedPersonRepository) {
        this.documentDataRepository = documentDataRepository;
        this.documentEquipmentRepository = documentEquipmentRepository;
        this.documentProductRepository = documentProductRepository;
        this.documentTrainedPersonRepository = documentTrainedPersonRepository;
    }

    @Transactional
    public DocumentDataDto create(DocumentDataDto dto) {
        if (dto.getId() != null) {
            return update(dto.getId(), dto);
        }

        DocumentData entity = DocumentDataMapper.toEntity(dto);

        // Save document first without children to get the ID
        List<DocumentEquipment> equipments = entity.getEquipments();
        entity.setEquipments(new ArrayList<>());
        List<DocumentProduct> products = entity.getProducts();
        entity.setProducts(new ArrayList<>());
        List<DocumentTrainedPerson> trainedPersons = entity.getTrainedPersons();
        entity.setTrainedPersons(new ArrayList<>());
        DocumentData saved = documentDataRepository.save(entity);

        // Save equipments
        if (equipments != null && !equipments.isEmpty()) {
            for (int i = 0; i < equipments.size(); i++) {
                DocumentEquipment eq = equipments.get(i);
                eq.setDocumentDataId(saved.getId());
                eq.setSortOrder(i);
            }
            documentEquipmentRepository.saveAll(equipments);
        }

        // Save products
        if (products != null && !products.isEmpty()) {
            for (int i = 0; i < products.size(); i++) {
                DocumentProduct pr = products.get(i);
                pr.setDocumentDataId(saved.getId());
                pr.setSortOrder(i);
            }
            documentProductRepository.saveAll(products);
        }

        // Save trained persons
        if (trainedPersons != null && !trainedPersons.isEmpty()) {
            for (int i = 0; i < trainedPersons.size(); i++) {
                DocumentTrainedPerson tp = trainedPersons.get(i);
                tp.setDocumentDataId(saved.getId());
                tp.setSortOrder(i);
            }
            documentTrainedPersonRepository.saveAll(trainedPersons);
        }

        // Reload to get children in the response
        return DocumentDataMapper.toDto(documentDataRepository.findById(saved.getId()).orElse(saved));
    }

    @Transactional(readOnly = true)
    public List<DocumentDataDto> getAll() {
        Pageable topFifty = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "contractDate"));
        Page<DocumentData> list = documentDataRepository.findAllByOrderByContractDateDesc(topFifty);
        return DocumentDataMapper.toDtoList(list.getContent());
    }

    @Transactional(readOnly = true)
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
        List<DocumentEquipment> currentEquipments = documentEquipmentRepository.findByDocumentDataIdOrderBySortOrderAsc(existing.getId());
        Set<Long> existingEquipmentIds = currentEquipments.stream()
                .map(DocumentEquipment::getId)
                .collect(Collectors.toSet());

        if (dto.getEquipments() != null && !dto.getEquipments().isEmpty()) {
            Set<Long> dtoEquipmentIds = dto.getEquipments().stream()
                    .map(DocumentEquipmentDto::getId)
                    .filter(eqId -> eqId != null)
                    .collect(Collectors.toSet());

            List<Long> idsToDelete = existingEquipmentIds.stream()
                    .filter(eqId -> !dtoEquipmentIds.contains(eqId))
                    .collect(Collectors.toList());

            if (!idsToDelete.isEmpty()) {
                documentEquipmentRepository.deleteAllByIdIn(idsToDelete);
            }

            List<DocumentEquipment> toSave = new ArrayList<>();
            for (int i = 0; i < dto.getEquipments().size(); i++) {
                DocumentEquipmentDto eqDto = dto.getEquipments().get(i);
                DocumentEquipment eq = DocumentEquipment.builder()
                        .id(eqDto.getId())
                        .documentDataId(existing.getId())
                        .equipmentId(eqDto.getEquipmentId())
                        .equipmentName(eqDto.getEquipmentName())
                        .productCode(eqDto.getProductCode())
                        .serialNumber(eqDto.getSerialNumber())
                        .sortOrder(i)
                        .build();
                toSave.add(eq);
            }

            if (!toSave.isEmpty()) {
                documentEquipmentRepository.saveAll(toSave);
            }
        } else {
            if (!existingEquipmentIds.isEmpty()) {
                documentEquipmentRepository.deleteAllByIdIn(new ArrayList<>(existingEquipmentIds));
            }
        }

        // Update products: keep existing, add new, remove deleted
        List<DocumentProduct> currentProducts = documentProductRepository.findByDocumentDataIdOrderBySortOrderAsc(existing.getId());
        Set<Long> existingProductIds = currentProducts.stream()
                .map(DocumentProduct::getId)
                .collect(Collectors.toSet());

        if (dto.getProducts() != null && !dto.getProducts().isEmpty()) {
            Set<Long> dtoProductIds = dto.getProducts().stream()
                    .map(DocumentProductDto::getId)
                    .filter(prId -> prId != null)
                    .collect(Collectors.toSet());

            List<Long> idsToDelete = existingProductIds.stream()
                    .filter(prId -> !dtoProductIds.contains(prId))
                    .collect(Collectors.toList());

            if (!idsToDelete.isEmpty()) {
                documentProductRepository.deleteAllByIdIn(idsToDelete);
            }

            List<DocumentProduct> toSave = new ArrayList<>();
            for (int i = 0; i < dto.getProducts().size(); i++) {
                DocumentProductDto prDto = dto.getProducts().get(i);
                DocumentProduct pr = DocumentProduct.builder()
                        .id(prDto.getId())
                        .documentDataId(existing.getId())
                        .productId(prDto.getProductId())
                        .productName(prDto.getProductName())
                        .productCod(prDto.getProductCod())
                        .quantity(prDto.getQuantity())
                        .sortOrder(i)
                        .build();
                toSave.add(pr);
            }

            if (!toSave.isEmpty()) {
                documentProductRepository.saveAll(toSave);
            }
        } else {
            if (!existingProductIds.isEmpty()) {
                documentProductRepository.deleteAllByIdIn(new ArrayList<>(existingProductIds));
            }
        }

        // Update trained persons: keep existing, add new, remove deleted
        List<DocumentTrainedPerson> currentTrainedPersons = documentTrainedPersonRepository.findByDocumentDataIdOrderBySortOrderAsc(existing.getId());
        Set<Long> existingTrainedPersonIds = currentTrainedPersons.stream()
                .map(DocumentTrainedPerson::getId)
                .collect(Collectors.toSet());

        if (dto.getTrainedPersons() != null && !dto.getTrainedPersons().isEmpty()) {
            Set<Long> dtoTrainedPersonIds = dto.getTrainedPersons().stream()
                    .map(DocumentTrainedPersonDto::getId)
                    .filter(tpId -> tpId != null)
                    .collect(Collectors.toSet());

            List<Long> idsToDelete = existingTrainedPersonIds.stream()
                    .filter(tpId -> !dtoTrainedPersonIds.contains(tpId))
                    .collect(Collectors.toList());

            if (!idsToDelete.isEmpty()) {
                documentTrainedPersonRepository.deleteAllByIdIn(idsToDelete);
            }

            List<DocumentTrainedPerson> toSave = new ArrayList<>();
            for (int i = 0; i < dto.getTrainedPersons().size(); i++) {
                DocumentTrainedPersonDto tpDto = dto.getTrainedPersons().get(i);
                DocumentTrainedPerson tp = DocumentTrainedPerson.builder()
                        .id(tpDto.getId())
                        .documentDataId(existing.getId())
                        .trainedPersonName(tpDto.getTrainedPersonName())
                        .jobFunction(tpDto.getJobFunction())
                        .phone(tpDto.getPhone())
                        .email(tpDto.getEmail())
                        .signatureBase64(tpDto.getSignatureBase64())
                        .sortOrder(i)
                        .build();
                toSave.add(tp);
            }

            if (!toSave.isEmpty()) {
                documentTrainedPersonRepository.saveAll(toSave);
            }
        } else {
            if (!existingTrainedPersonIds.isEmpty()) {
                documentTrainedPersonRepository.deleteAllByIdIn(new ArrayList<>(existingTrainedPersonIds));
            }
        }

        existing.setSignatureDate(dto.getSignatureDate());
        existing.setContactPerson(dto.getContactPerson());

        DocumentData updated = documentDataRepository.save(existing);
        return DocumentDataMapper.toDto(updated);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!documentDataRepository.existsById(id)) {
            return false;
        }
        documentEquipmentRepository.deleteByDocumentDataId(id);
        documentProductRepository.deleteByDocumentDataId(id);
        documentTrainedPersonRepository.deleteByDocumentDataId(id);
        documentDataRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
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

        // Try filesystem first (for development - picks up changes without restart),
        // fall back to classpath (for production / packaged JAR)
        Resource templateResource;
        File fsFile = new File("ServiceApp/src/main/resources/templates/" + templateFile);
        if (fsFile.exists()) {
            templateResource = new FileSystemResource(fsFile);
            System.out.println("Loading template from filesystem: " + fsFile.getAbsolutePath());
        } else {
            templateResource = new ClassPathResource("templates/" + templateFile);
            System.out.println("Loading template from classpath: templates/" + templateFile);
        }

        try (InputStream templateStream = templateResource.getInputStream();
             XWPFDocument document = new XWPFDocument(templateStream)) {

            // Generate dynamic table rows BEFORE placeholder replacement
            generateEquipmentRows(document, data.getEquipments());
            generateProductRows(document, data.getProducts());
            generateTrainedPersonRows(document, data.getTrainedPersons());

            // Replace remaining placeholders in text
            replacePlaceholders(document, buildValueMap(data));

            // Add page numbers to footer
            addPageNumbers(document);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);

            return out;
        }
    }

    private void generateEquipmentRows(XWPFDocument document, List<DocumentEquipmentDto> equipments) {
        System.out.println("=== GENERATE EQUIPMENT ROWS ===");
        System.out.println("Equipments: " + (equipments != null ? equipments.size() : "null"));
        if (equipments == null || equipments.isEmpty()) {
            System.out.println("No equipments, returning.");
            return;
        }

        System.out.println("Tables in document: " + document.getTables().size());
        for (XWPFTable table : document.getTables()) {
            int templateRowIndex = findTemplateRow(table);
            System.out.println("Table rows: " + table.getRows().size() + ", templateRowIndex: " + templateRowIndex);
            if (templateRowIndex == -1) {
                continue;
            }

            XWPFTableRow templateRow = table.getRow(templateRowIndex);
            // Save original template XML BEFORE modifying the template row
            CTRow originalTemplateCTRow = (CTRow) templateRow.getCtRow().copy();

            // Replace first equipment in the template row (via POI wrappers - works fine)
            replaceInRow(templateRow, equipments.get(0), 1);
            System.out.println("  Replaced equipment 0 in template row " + templateRowIndex);

            // For additional equipments, clone the ORIGINAL template XML and replace via XML string
            for (int i = 1; i < equipments.size(); i++) {
                CTRow copiedCTRow = (CTRow) originalTemplateCTRow.copy();
                replaceInCTRow(copiedCTRow, equipments.get(i), i + 1);
                table.getCTTbl().addNewTr().set(copiedCTRow);
                System.out.println("  Added equipment " + i + " row via XML");
            }
        }
    }

    private void generateProductRows(XWPFDocument document, List<DocumentProductDto> products) {
        if (products == null || products.isEmpty()) {
            for (XWPFTable table : document.getTables()) {
                int templateRowIndex = findProductTemplateRow(table);
                if (templateRowIndex == -1) continue;
                XWPFTableRow templateRow = table.getRow(templateRowIndex);
                for (XWPFTableCell cell : templateRow.getTableCells()) {
                    for (XWPFParagraph para : cell.getParagraphs()) {
                        for (XWPFRun run : para.getRuns()) {
                            run.setText("", 0);
                        }
                    }
                }
            }
            return;
        }

        for (XWPFTable table : document.getTables()) {
            int templateRowIndex = findProductTemplateRow(table);
            if (templateRowIndex == -1) {
                continue;
            }

            XWPFTableRow templateRow = table.getRow(templateRowIndex);
            CTRow originalTemplateCTRow = (CTRow) templateRow.getCtRow().copy();

            replaceInProductRow(templateRow, products.get(0), 1);

            for (int i = 1; i < products.size(); i++) {
                CTRow copiedCTRow = (CTRow) originalTemplateCTRow.copy();
                replaceInProductCTRow(copiedCTRow, products.get(i), i + 1);
                table.getCTTbl().addNewTr().set(copiedCTRow);
            }
        }
    }

    private int findProductTemplateRow(XWPFTable table) {
        for (int i = 0; i < table.getRows().size(); i++) {
            XWPFTableRow row = table.getRow(i);
            for (XWPFTableCell cell : row.getTableCells()) {
                String text = cell.getText();
                if (text != null && (text.contains("${productName}") ||
                        text.contains("${productCod}") ||
                        text.contains("${productQuantity}"))) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void replaceInProductRow(XWPFTableRow row, DocumentProductDto pr, int index) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("${productName}", pr.getProductName() != null ? pr.getProductName() : "");
        replacements.put("${productCod}", pr.getProductCod() != null ? pr.getProductCod() : "");
        replacements.put("${productQuantity}", pr.getQuantity() != null ? String.valueOf(pr.getQuantity()) : "");
        replacements.put("${nr}", String.valueOf(index));

        for (XWPFTableCell cell : row.getTableCells()) {
            replaceCellContent(cell, replacements);
        }
    }

    private void replaceInProductCTRow(CTRow ctRow, DocumentProductDto pr, int index) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("${nr}", String.valueOf(index));
        replacements.put("${productName}", pr.getProductName() != null ? pr.getProductName() : "");
        replacements.put("${productCod}", pr.getProductCod() != null ? pr.getProductCod() : "");
        replacements.put("${productQuantity}", pr.getQuantity() != null ? String.valueOf(pr.getQuantity()) : "");

        String xml = ctRow.xmlText();
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            xml = xml.replace(entry.getKey(), entry.getValue());
        }
        try {
            CTRow newRow = CTRow.Factory.parse(xml);
            ctRow.set(newRow);
        } catch (Exception e) {
            System.err.println("Error parsing product CTRow XML: " + e.getMessage());
        }
    }

    private void generateTrainedPersonRows(XWPFDocument document, List<DocumentTrainedPersonDto> trainedPersons) {
        System.out.println("=== GENERATE TRAINED PERSON ROWS ===");
        System.out.println("TrainedPersons: " + (trainedPersons != null ? trainedPersons.size() : "null"));
        if (trainedPersons == null || trainedPersons.isEmpty()) {
            System.out.println("No trained persons, clearing template row.");
            for (XWPFTable table : document.getTables()) {
                int templateRowIndex = findTrainedPersonTemplateRow(table);
                if (templateRowIndex == -1) continue;
                XWPFTableRow templateRow = table.getRow(templateRowIndex);
                for (XWPFTableCell cell : templateRow.getTableCells()) {
                    for (XWPFParagraph para : cell.getParagraphs()) {
                        for (XWPFRun run : para.getRuns()) {
                            run.setText("", 0);
                        }
                    }
                }
            }
            return;
        }

        for (XWPFTable table : document.getTables()) {
            int templateRowIndex = findTrainedPersonTemplateRow(table);
            if (templateRowIndex == -1) {
                continue;
            }

            // Remove any pre-existing empty rows below the template row
            int totalRows = table.getRows().size();
            for (int r = totalRows - 1; r > templateRowIndex; r--) {
                table.removeRow(r);
            }

            XWPFTableRow templateRow = table.getRow(templateRowIndex);
            // Save original template XML BEFORE modifying
            CTRow originalTemplateCTRow = (CTRow) templateRow.getCtRow().copy();

            // Replace first trained person in template row
            replaceInTrainedPersonRow(templateRow, trainedPersons.get(0), 1);
            System.out.println("  Replaced trained person 0 in template row " + templateRowIndex);

            // For additional trained persons, clone original template and replace via XML
            for (int i = 1; i < trainedPersons.size(); i++) {
                CTRow copiedCTRow = (CTRow) originalTemplateCTRow.copy();
                replaceInCTRow(copiedCTRow, trainedPersons.get(i), i + 1, true);
                table.getCTTbl().addNewTr().set(copiedCTRow);
                System.out.println("  Added trained person " + i + " row via XML");
            }
        }
    }

    private int findTrainedPersonTemplateRow(XWPFTable table) {
        for (int i = 0; i < table.getRows().size(); i++) {
            XWPFTableRow row = table.getRow(i);
            for (XWPFTableCell cell : row.getTableCells()) {
                String text = cell.getText();
                if (text != null && (text.contains("${trainedPersons}") ||
                        text.contains("${jobFunction}") ||
                        text.contains("${signatureBase64}"))) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void replaceInTrainedPersonRow(XWPFTableRow row, DocumentTrainedPersonDto tp, int index) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("${trainedPersons}", tp.getTrainedPersonName() != null ? tp.getTrainedPersonName() : "");
        replacements.put("${jobFunction}", tp.getJobFunction() != null ? tp.getJobFunction() : "");
        replacements.put("${signatureBase64}", "");
        replacements.put("${nr}", String.valueOf(index));

        for (XWPFTableCell cell : row.getTableCells()) {
            replaceCellContent(cell, replacements);
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

    private void replaceInCTRow(CTRow ctRow, DocumentEquipmentDto eq, int index) {
        replaceInCTRow(ctRow, eq, index, false);
    }

    private void replaceInCTRow(CTRow ctRow, Object dto, int index, boolean isTrainedPerson) {
        // Build replacement map
        Map<String, String> replacements = new HashMap<>();
        replacements.put("${nr}", String.valueOf(index));

        if (isTrainedPerson && dto instanceof DocumentTrainedPersonDto tp) {
            replacements.put("${trainedPersons}", tp.getTrainedPersonName() != null ? tp.getTrainedPersonName() : "");
            replacements.put("${jobFunction}", tp.getJobFunction() != null ? tp.getJobFunction() : "");
            replacements.put("${signatureBase64}", "");
        } else if (dto instanceof DocumentEquipmentDto eq) {
            replacements.put("${nameOfEquipment}", eq.getEquipmentName() != null ? eq.getEquipmentName() : "");
            replacements.put("${equipmentName}", eq.getEquipmentName() != null ? eq.getEquipmentName() : "");
            replacements.put("${productCode}", eq.getProductCode() != null ? eq.getProductCode() : "");
            replacements.put("${serialNumber}", eq.getSerialNumber() != null ? eq.getSerialNumber() : "");
        }

        // Work directly with the XML string of the row
        String xml = ctRow.xmlText();
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            xml = xml.replace(entry.getKey(), entry.getValue());
        }
        try {
            CTRow newRow = CTRow.Factory.parse(xml);
            ctRow.set(newRow);
        } catch (Exception e) {
            System.err.println("Error parsing CTRow XML: " + e.getMessage());
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

    private void addPageNumbers(XWPFDocument document) {
        // Build footer paragraph XML: "Pagina X din Y" centered
        CTP ctp = CTP.Factory.newInstance();

        // Center-align
        CTPPr ppr = ctp.addNewPPr();
        ppr.addNewJc().setVal(STJc.CENTER);

        // "Pagina " text
        CTR ctr1 = ctp.addNewR();
        ctr1.addNewRPr().addNewSz().setVal(java.math.BigInteger.valueOf(18));
        ctr1.addNewT().setStringValue("Pagina ");

        // PAGE field begin
        CTR ctr2 = ctp.addNewR();
        ctr2.addNewFldChar().setFldCharType(STFldCharType.BEGIN);

        // PAGE instruction
        CTR ctr3 = ctp.addNewR();
        CTText instrPage = ctr3.addNewInstrText();
        instrPage.setStringValue(" PAGE ");

        // PAGE field end
        CTR ctr4 = ctp.addNewR();
        ctr4.addNewFldChar().setFldCharType(STFldCharType.END);

        // " din " text
        CTR ctr5 = ctp.addNewR();
        ctr5.addNewRPr().addNewSz().setVal(java.math.BigInteger.valueOf(18));
        ctr5.addNewT().setStringValue(" din ");

        // NUMPAGES field begin
        CTR ctr6 = ctp.addNewR();
        ctr6.addNewFldChar().setFldCharType(STFldCharType.BEGIN);

        // NUMPAGES instruction
        CTR ctr7 = ctp.addNewR();
        CTText instrNum = ctr7.addNewInstrText();
        instrNum.setStringValue(" NUMPAGES ");

        // NUMPAGES field end
        CTR ctr8 = ctp.addNewR();
        ctr8.addNewFldChar().setFldCharType(STFldCharType.END);

        // Create footer via XWPFHeaderFooterPolicy
        CTSectPr sectPr = document.getDocument().getBody().isSetSectPr()
                ? document.getDocument().getBody().getSectPr()
                : document.getDocument().getBody().addNewSectPr();

        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);
        policy.createFooter(STHdrFtr.DEFAULT, new XWPFParagraph[]{new XWPFParagraph(ctp, document)});
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
        map.put("contactPerson", data.getContactPerson() != null ? data.getContactPerson() : "");

        // Get first trained person's data for standalone placeholders (e.g. certificat_garantie)
        // Table-based trained person placeholders are handled by generateTrainedPersonRows
        if (data.getTrainedPersons() != null && !data.getTrainedPersons().isEmpty()) {
            DocumentTrainedPersonDto first = data.getTrainedPersons().get(0);
            map.put("trainedPerson", first.getTrainedPersonName() != null ? first.getTrainedPersonName() : "");
            map.put("function", first.getJobFunction() != null ? first.getJobFunction() : "");
            map.put("phone", first.getPhone() != null ? first.getPhone() : "");
            map.put("email", first.getEmail() != null ? first.getEmail() : "");
        } else {
            map.put("trainedPerson", "");
            map.put("function", "");
            map.put("phone", "");
            map.put("email", "");
        }

        return map;
    }
}
