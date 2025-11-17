package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.DocumentDataDto;
import com.example.ServiceApp.entity.DocumentData;
import com.example.ServiceApp.entity.InterventionSheet;
import com.example.ServiceApp.mapper.DocumentDataMapper;
import com.example.ServiceApp.repository.DocumentDataRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.cglib.core.Local;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DocumentDataService {

    private final DocumentDataRepository documentDataRepository;

    public DocumentDataService(DocumentDataRepository documentDataRepository) {
        this.documentDataRepository = documentDataRepository;
    }

    // ✅ CREATE
    public DocumentDataDto create(DocumentDataDto dto) {
        DocumentData entity = DocumentDataMapper.toEntity(dto);
        if (dto.getId() == null) {
            DocumentData saved = documentDataRepository.save(entity);
            System.out.println(dto.getCustomerName());
            return DocumentDataMapper.toDto(saved);
        } else {
            update(dto.getId(), dto);
        }
        return DocumentDataMapper.toDto(entity);
    }

    // ✅ READ (all)
    public List<DocumentDataDto> getAll() {
        Pageable topFifty = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "contractDate"));
        Page<DocumentData> list = documentDataRepository.findAllByOrderByContractDateDesc(topFifty);
        return DocumentDataMapper.toDtoList(list.getContent());
    }

    // ✅ READ (by id)
    public DocumentDataDto getById(Long id) {
        Optional<DocumentData> optional = documentDataRepository.findById(id);
        return optional.map(DocumentDataMapper::toDto).orElse(null);
    }

    // ✅ UPDATE
    public DocumentDataDto update(Long id, DocumentDataDto dto) {
        Optional<DocumentData> optional = documentDataRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        DocumentData existing = optional.get();
        // actualizăm câmpurile
        existing.setCustomerId(dto.getCustomerId());
        existing.setCui(dto.getCui());
        existing.setContractDate(dto.getContractDate());
        existing.setMonthOfWarranty(dto.getMonthOfWarranty());
        existing.setMonthOfWarrantyHandPieces(dto.getMonthOfWarrantyHandPieces());
        existing.setNumberOfContract(dto.getNumberOfContract());

        existing.setEquipmentId1(dto.getEquipmentId1());
        existing.setEquipmentId2(dto.getEquipmentId2());
        existing.setEquipmentId3(dto.getEquipmentId3());
        existing.setEquipmentId4(dto.getEquipmentId4());
        existing.setEquipmentId5(dto.getEquipmentId5());
        existing.setEquipmentId6(dto.getEquipmentId6());

        existing.setProductCode1(dto.getProductCode1());
        existing.setProductCode2(dto.getProductCode2());
        existing.setProductCode3(dto.getProductCode3());
        existing.setProductCode4(dto.getProductCode4());
        existing.setProductCode5(dto.getProductCode5());
        existing.setProductCode6(dto.getProductCode6());

        existing.setSerialNumber1(dto.getSerialNumber1());
        existing.setSerialNumber2(dto.getSerialNumber2());
        existing.setSerialNumber3(dto.getSerialNumber3());
        existing.setSerialNumber4(dto.getSerialNumber4());
        existing.setSerialNumber5(dto.getSerialNumber5());
        existing.setSerialNumber6(dto.getSerialNumber6());

        existing.setSignatureDate(dto.getSignatureDate());
        existing.setTrainedPerson(dto.getTrainedPerson());
        existing.setJobFunction(dto.getJobFunction());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setContactPerson(dto.getContactPerson());

        DocumentData updated = documentDataRepository.save(existing);
        return DocumentDataMapper.toDto(updated);
    }

    // ✅ DELETE
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
        // 1️⃣ Alegem fișierul template
        String templateFile = switch (type.toLowerCase()) {
            case "predare" -> "pv_predare_primire.docx";
            case "garantie" -> "certificat_garantie.docx";
            case "punere" -> "pv_punere_in_functiune.docx";
            case "receptie" -> "pv_receptie.docx";
            default -> throw new IllegalArgumentException("Tip necunoscut: " + type);
        };

        // 2️⃣ Încărcăm template-ul original (cu imagine, formatare etc.)
        try (InputStream templateStream = new ClassPathResource("templates/" + templateFile).getInputStream();
             XWPFDocument document = new XWPFDocument(templateStream)) {

            // 3️⃣ Înlocuim doar textul (fără să atingem alt conținut)
            replacePlaceholders(document, buildValueMap(data));

            // 4️⃣ Scriem documentul completat într-un ByteArrayOutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);

            return out;
        }
    }

    private void replacePlaceholders(XWPFDocument document, Map<String, String> values) {
        // Normalize keys: ensure map has keys with ${...} form as well as original
        Map<String, String> placeholders = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : values.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            if (k == null) continue;
            if (k.startsWith("${") && k.endsWith("}")) {
                placeholders.put(k, v);
                // also put bare name (optional)
                String bare = k.substring(2, k.length()-1);
                placeholders.putIfAbsent(bare, v);
            } else {
                placeholders.put("${" + k + "}", v);
                placeholders.putIfAbsent(k, v);
            }
        }

        // paragraphs outside tables
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceInParagraph(paragraph, placeholders);
        }

        // paragraphs inside tables
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceInParagraph(paragraph, placeholders);
                    }
                }
            }
        }

        // headers and footers (optional but often needed)
        try {
            for (XWPFHeader header : document.getHeaderList()) {
                for (XWPFParagraph p : header.getParagraphs()) replaceInParagraph(p, placeholders);
            }
            for (XWPFFooter footer : document.getFooterList()) {
                for (XWPFParagraph p : footer.getParagraphs()) replaceInParagraph(p, placeholders);
            }
        } catch (UnsupportedOperationException ignored) {
            // some documents may not support headers/footers in this context
        }
    }

    /**
     * Replace placeholders inside a single paragraph, supporting placeholders spanning multiple runs.
     */
    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> placeholders) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        // Build array of text pieces and cumulative lengths
        List<String> runTexts = new ArrayList<>(runs.size());
        for (XWPFRun r : runs) {
            String t = r.getText(0);
            runTexts.add(t == null ? "" : t);
        }

        StringBuilder concat = new StringBuilder();
        for (String s : runTexts) concat.append(s);
        String fullText = concat.toString();
        if (fullText.isEmpty()) return;

        // Find next placeholder occurrence (left to right). We'll loop until none remain.
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

            if (foundPos == -1) break; // no more placeholders

            int startIndex = foundPos;
            int endIndex = startIndex + foundKey.length(); // exclusive

            // map startIndex and endIndex to run indices and offsets
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
            // Edge cases: placeholder starts/ends at run boundaries
            if (runStart == -1) {
                // starts at or after concatenated length? skip (defensive)
                break;
            }
            if (runEnd == -1) {
                // ends beyond last run -> set to last run end
                runEnd = runTexts.size() - 1;
                offsetInRunEnd = runTexts.get(runEnd).length();
            }

            // Build new texts for runStart and runEnd (and remove middle runs)
            String startRunText = runTexts.get(runStart);
            String endRunText = runTexts.get(runEnd);

            String prefix = startRunText.substring(0, offsetInRunStart);
            String suffix = endRunText.substring(offsetInRunEnd);

            String replacement = foundValue; // already ensured non-null string

            String newRunText = prefix + replacement + suffix;

            // Apply changes:
            // 1) set text of runStart to newRunText
            // 2) remove runs from runStart+1 to runEnd (inclusive), because content moved into runStart
            // Note: need to preserve formatting of original runStart (we are modifying text only)
            XWPFRun targetRun = runs.get(runStart);
            // set text in target run (replace existing)
            setRunText(targetRun, newRunText);

            // remove subsequent runs (from runEnd down to runStart+1)
            for (int rem = runEnd; rem > runStart; rem--) {
                paragraph.removeRun(rem);
            }

            // Recompute runTexts and fullText to continue loop
            // rebuild runTexts from paragraph.getRuns() (structure changed)
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

    /**
     * Helper to set the text of a run reliably (clears existing text and sets new one).
     */
    private void setRunText(XWPFRun run, String text) {
        // Clear existing texts (some runs may have multiple text nodes)
        int textCount = run.getCTR().sizeOfTArray();
        for (int i = textCount - 1; i >= 0; i--) {
            run.getCTR().removeT(i);
        }
        // create new text node with the content
        if (text != null && !text.isEmpty()) {
            XWPFRun newRun = run; // reuse same run object (preserves formatting)
            newRun.setText(text, 0);
        } else {
            // If empty string, ensure there's an empty text node to avoid null issues
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

        map.put("nameOfEquipment1", data.getEquipmentName1() != null ? data.getEquipmentName1() : "");
        map.put("nameOfEquipment2", data.getEquipmentName2() != null ? data.getEquipmentName2() : "");
        map.put("nameOfEquipment3", data.getEquipmentName3() != null ? data.getEquipmentName3() : "");
        map.put("nameOfEquipment4", data.getEquipmentName4() != null ? data.getEquipmentName4() : "");
        map.put("nameOfEquipment5", data.getEquipmentName5() != null ? data.getEquipmentName5() : "");
        map.put("nameOfEquipment6", data.getEquipmentName6() != null ? data.getEquipmentName6() : "");

        map.put("productCode1", data.getProductCode1() != null ? data.getProductCode1() : "");
        map.put("productCode2", data.getProductCode2() != null ? data.getProductCode2() : "");
        map.put("productCode3", data.getProductCode3() != null ? data.getProductCode3() : "");
        map.put("productCode4", data.getProductCode4() != null ? data.getProductCode4() : "");
        map.put("productCode5", data.getProductCode5() != null ? data.getProductCode5() : "");
        map.put("productCode6", data.getProductCode6() != null ? data.getProductCode6() : "");

        map.put("serialNumber1", data.getSerialNumber1() != null ? data.getSerialNumber1() : "");
        map.put("serialNumber2", data.getSerialNumber2() != null ? data.getSerialNumber2() : "");
        map.put("serialNumber3", data.getSerialNumber3() != null ? data.getSerialNumber3() : "");
        map.put("serialNumber4", data.getSerialNumber4() != null ? data.getSerialNumber4() : "");
        map.put("serialNumber5", data.getSerialNumber5() != null ? data.getSerialNumber5() : "");
        map.put("serialNumber6", data.getSerialNumber6() != null ? data.getSerialNumber6() : "");

        map.put("signatureDate", data.getSignatureDate() != null ? data.getSignatureDate().format(formatter) : "");
        map.put("trainedPerson", data.getTrainedPerson() != null ? data.getTrainedPerson() : "");
        map.put("function", data.getJobFunction() != null ? data.getJobFunction() : "");
        map.put("phone", data.getPhone() != null ? data.getPhone() : "");
        map.put("contactPerson", data.getContactPerson() != null ? data.getContactPerson() : "");
        return map;
    }



}
