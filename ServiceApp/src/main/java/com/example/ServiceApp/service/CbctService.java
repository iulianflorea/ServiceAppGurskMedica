package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.*;
import com.example.ServiceApp.entity.*;
import com.example.ServiceApp.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CbctService {

    private static final Map<String, List<String>> MODE_GENDERS;
    static {
        MODE_GENDERS = new LinkedHashMap<>();
        MODE_GENDERS.put("CT",    Arrays.asList("BARBAT", "FEMEIE", "COPIL"));
        MODE_GENDERS.put("PANO",  Arrays.asList("BARBAT", "FEMEIE", "COPIL"));
        MODE_GENDERS.put("CEPH",  Arrays.asList("BARBAT", "FEMEIE", "COPIL"));
        MODE_GENDERS.put("RETRO", Arrays.asList("ADULT", "COPIL"));
    }

    private final CbctDeviceRepository deviceRepository;
    private final CbctDeviceReferenceRepository deviceReferenceRepository;
    private final CbctMeasurementRepository measurementRepository;
    private final CbctMeasurementValueRepository measurementValueRepository;
    private final CbctDozimetrieRepository dozimetrieRepository;

    // ── Device CRUD ──────────────────────────────────────────────────────────

    public List<CbctDeviceDto> findAllDevices() {
        return deviceRepository.findAllByOrderByBrandAscModelAsc()
                .stream().map(this::toDeviceDto).collect(Collectors.toList());
    }

    public CbctDeviceDto findDeviceById(Long id) {
        return toDeviceDto(deviceRepository.findById(id).orElseThrow());
    }

    @Transactional
    public CbctDeviceDto saveDevice(CbctDeviceDto dto) {
        CbctDevice device = dto.getId() != null
                ? deviceRepository.findById(dto.getId()).orElseThrow()
                : new CbctDevice();
        device.setBrand(dto.getBrand());
        device.setModel(dto.getModel());
        CbctDevice saved = deviceRepository.save(device);

        deviceReferenceRepository.deleteByDeviceId(saved.getId());
        if (dto.getReferences() != null) {
            for (CbctReferenceDto ref : dto.getReferences()) {
                deviceReferenceRepository.save(CbctDeviceReference.builder()
                        .deviceId(saved.getId()).mode(ref.getMode()).gender(ref.getGender())
                        .kvp(ref.getKvp()).current(ref.getCurrent())
                        .scanTime(ref.getScanTime()).dap(ref.getDap()).build());
            }
        }
        return toDeviceDto(saved);
    }

    @Transactional
    public void deleteDevice(Long id) {
        deviceReferenceRepository.deleteByDeviceId(id);
        deviceRepository.deleteById(id);
    }

    // ── Measurement CRUD ─────────────────────────────────────────────────────

    public List<CbctMeasurementDto> findAllMeasurements() {
        return measurementRepository
                .findAllByOrderByMeasurementDateDescIdDesc(org.springframework.data.domain.PageRequest.of(0, 50))
                .stream().map(this::toMeasurementDto).collect(Collectors.toList());
    }

    public List<CbctMeasurementDto> searchMeasurements(String keyword) {
        return measurementRepository
                .search(keyword)
                .stream().map(this::toMeasurementDto).collect(Collectors.toList());
    }

    public CbctMeasurementDto findMeasurementById(Long id) {
        return toMeasurementDto(measurementRepository.findById(id).orElseThrow());
    }

    @Transactional
    public CbctMeasurementDto saveMeasurement(CbctMeasurementDto dto) {
        CbctMeasurement m = dto.getId() != null
                ? measurementRepository.findById(dto.getId()).orElseThrow()
                : new CbctMeasurement();
        m.setCustomerId(dto.getCustomerId());
        m.setDeviceId(dto.getDeviceId());
        m.setSerialNumber(dto.getSerialNumber());
        m.setMeasurementDate(dto.getMeasurementDate());
        CbctMeasurement saved = measurementRepository.save(m);

        measurementValueRepository.deleteByMeasurementId(saved.getId());
        if (dto.getValues() != null) {
            for (CbctMeasurementValueDto vDto : dto.getValues()) {
                measurementValueRepository.save(CbctMeasurementValue.builder()
                        .measurementId(saved.getId()).mode(vDto.getMode()).gender(vDto.getGender())
                        .kvp(vDto.getKvp()).scanTime(vDto.getScanTime())
                        .mgy(vDto.getMgy()).mmAiHvl(vDto.getMmAiHvl()).uGyPerS(vDto.getUGyPerS())
                        .pulses(vDto.getPulses()).mmAiTf(vDto.getMmAiTf()).build());
            }
        }

        dozimetrieRepository.deleteByMeasurementId(saved.getId());
        if (dto.getDozimetrie() != null) {
            for (CbctDozimetrieDto dDto : dto.getDozimetrie()) {
                dozimetrieRepository.save(CbctDozimetrie.builder()
                        .measurementId(saved.getId())
                        .punctMasurat(dDto.getPunctMasurat())
                        .valoareaMaximaMarsurata(dDto.getValoareaMaximaMarsurata())
                        .materialPerete(dDto.getMaterialPerete()).build());
            }
        }
        return toMeasurementDto(measurementRepository.findById(saved.getId()).orElseThrow());
    }

    @Transactional
    public void deleteMeasurement(Long id) {
        measurementValueRepository.deleteByMeasurementId(id);
        dozimetrieRepository.deleteByMeasurementId(id);
        measurementRepository.deleteById(id);
    }

    // ── Global Excel Export ───────────────────────────────────────────────────

    public ResponseEntity<byte[]> exportExcel() throws Exception {
        List<CbctMeasurement> measurements = measurementRepository.findAllByOrderByMeasurementDateDescIdDesc();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Masuratori CBCT");

            CellStyle titleStyle = titleStyle(wb);
            CellStyle hdrStyle = headerStyle(wb);
            CellStyle dataStyle = dataStyle(wb);
            CellStyle altDataStyle = altDataStyle(wb);

            // Title
            Row r0 = sheet.createRow(0);
            r0.setHeightInPoints(22);
            Cell t = r0.createCell(0);
            t.setCellValue("Registru Masuratori CBCT");
            t.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 19));

            // Column headers
            String[] hdrs = {"ID", "Client", "Brand", "Model", "Serie", "Data",
                    "Mod", "Gen", "Kvp ref.", "Kvp mas.",
                    "Timp ref. (s)", "Timp mas. (s)", "DAP ref. (mGy\u00b7cm\u00b2)", "mGy",
                    "mm AI HVL", "\u00b5Gy/s", "pulses", "mm AI TF",
                    "Punct masurat", "Val.max.mas. (\u00b5Sv/h)"};
            Row hRow = sheet.createRow(1);
            hRow.setHeightInPoints(18);
            for (int i = 0; i < hdrs.length; i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(hdrs[i]);
                c.setCellStyle(hdrStyle);
            }

            int rowNum = 2;
            boolean alt = false;
            for (CbctMeasurement m : measurements) {
                List<CbctMeasurementValue> vals = measurementValueRepository.findByMeasurementId(m.getId());
                List<CbctDozimetrie> doz = dozimetrieRepository.findByMeasurementId(m.getId());
                String customerName = m.getCustomer() != null ? m.getCustomer().getName() : "";
                String brand = m.getDevice() != null ? m.getDevice().getBrand() : "";
                String model = m.getDevice() != null ? m.getDevice().getModel() : "";
                String date = m.getMeasurementDate() != null ? m.getMeasurementDate().format(fmt) : "";
                List<CbctDeviceReference> refs = deviceReferenceRepository.findByDeviceId(m.getDeviceId() != null ? m.getDeviceId() : -1L);
                CellStyle ds = alt ? altDataStyle : dataStyle;
                alt = !alt;

                int maxRows = Math.max(vals.isEmpty() ? 1 : vals.size(), doz.isEmpty() ? 1 : doz.size());
                for (int i = 0; i < maxRows; i++) {
                    Row row = sheet.createRow(rowNum++);
                    row.setHeightInPoints(15);
                    if (i == 0) {
                        setCell(row, 0, m.getId(), ds);
                        setCell(row, 1, customerName, ds);
                        setCell(row, 2, brand, ds);
                        setCell(row, 3, model, ds);
                        setCell(row, 4, nullStr(m.getSerialNumber()), ds);
                        setCell(row, 5, date, ds);
                    } else {
                        for (int c = 0; c <= 5; c++) row.createCell(c).setCellStyle(ds);
                    }
                    if (i < vals.size()) {
                        CbctMeasurementValue v = vals.get(i);
                        CbctDeviceReference ref = refs.stream()
                                .filter(r -> r.getMode().equals(v.getMode()) && r.getGender().equals(v.getGender()))
                                .findFirst().orElse(null);
                        setCell(row, 6, nullStr(v.getMode()), ds);
                        setCell(row, 7, nullStr(v.getGender()), ds);
                        setCell(row, 8, ref != null && ref.getKvp() != null ? ref.getKvp() : null, ds);
                        setCell(row, 9, v.getKvp(), ds);
                        setCell(row, 10, ref != null && ref.getScanTime() != null ? ref.getScanTime() : null, ds);
                        setCell(row, 11, v.getScanTime(), ds);
                        setCell(row, 12, ref != null && ref.getDap() != null ? ref.getDap() : null, ds);
                        setCell(row, 13, v.getMgy(), ds);
                        setCell(row, 14, v.getMmAiHvl(), ds);
                        setCell(row, 15, v.getUGyPerS(), ds);
                        setCell(row, 16, v.getPulses(), ds);
                        setCell(row, 17, v.getMmAiTf(), ds);
                    } else {
                        for (int c = 6; c <= 17; c++) row.createCell(c).setCellStyle(ds);
                    }
                    if (i < doz.size()) {
                        setCell(row, 18, nullStr(doz.get(i).getPunctMasurat()), ds);
                        setCell(row, 19, doz.get(i).getValoareaMaximaMarsurata(), ds);
                    } else {
                        row.createCell(18).setCellStyle(ds);
                        row.createCell(19).setCellStyle(ds);
                    }
                }
            }

            for (int i = 0; i < hdrs.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
            return excelResponse(out.toByteArray(), "masuratori_cbct.xlsx");
        }
    }

    // ── Individual Excel Export ───────────────────────────────────────────────

    public ResponseEntity<byte[]> exportMeasurementExcel(Long id) throws Exception {
        CbctMeasurement m = measurementRepository.findById(id).orElseThrow();
        List<CbctMeasurementValue> vals = measurementValueRepository.findByMeasurementId(id);
        List<CbctDozimetrie> doz = dozimetrieRepository.findByMeasurementId(id);
        List<CbctDeviceReference> refs = deviceReferenceRepository.findByDeviceId(m.getDeviceId() != null ? m.getDeviceId() : -1L);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Masuratori");
            sheet.setColumnWidth(0, 5000);
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 4500);
            sheet.setColumnWidth(3, 4500);
            sheet.setColumnWidth(4, 4500);
            sheet.setColumnWidth(5, 4500);
            sheet.setColumnWidth(6, 4500);
            sheet.setColumnWidth(7, 4500);
            sheet.setColumnWidth(8, 4500);
            sheet.setColumnWidth(9, 4500);
            sheet.setColumnWidth(10, 4500);
            sheet.setColumnWidth(11, 4500);

            CellStyle titleStyle = titleStyle(wb);
            CellStyle hdrStyle = headerStyle(wb);
            CellStyle infoLblStyle = infoLabelStyle(wb);
            CellStyle infoValStyle = infoValueStyle(wb);
            CellStyle sectionStyle = sectionHeaderStyle(wb);
            CellStyle dataStyle = dataStyle(wb);
            CellStyle altStyle = altDataStyle(wb);
            CellStyle refStyle = refStyle(wb);

            int row = 0;

            // Title
            Row titleRow = sheet.createRow(row++);
            titleRow.setHeightInPoints(28);
            Cell tc = titleRow.createCell(0);
            tc.setCellValue("RAPORT MASURATORI CBCT  #" + m.getId());
            tc.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));

            // Empty row
            sheet.createRow(row++).setHeightInPoints(6);

            // Info section header
            Row infoHdr = sheet.createRow(row++);
            infoHdr.setHeightInPoints(18);
            Cell ih = infoHdr.createCell(0);
            ih.setCellValue("INFORMATII GENERALE");
            ih.setCellStyle(sectionStyle);
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 9));

            String customerName = m.getCustomer() != null ? m.getCustomer().getName() : "-";
            String brand = m.getDevice() != null ? m.getDevice().getBrand() : "-";
            String model = m.getDevice() != null ? m.getDevice().getModel() : "-";
            String date = m.getMeasurementDate() != null ? m.getMeasurementDate().format(fmt) : "-";

            // Info rows
            row = addInfoRow(sheet, row, "Client:", customerName, "Echipament:", brand + " " + model, infoLblStyle, infoValStyle);
            row = addInfoRow(sheet, row, "Numar serie:", nullStr(m.getSerialNumber()), "Data:", date, infoLblStyle, infoValStyle);

            // Empty row
            sheet.createRow(row++).setHeightInPoints(8);

            // Values section
            Row valHdr = sheet.createRow(row++);
            valHdr.setHeightInPoints(18);
            Cell vh = valHdr.createCell(0);
            vh.setCellValue("VALORI MASURATE");
            vh.setCellStyle(sectionStyle);
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 9));

            // Values column headers - two rows
            Row vColHdr1 = sheet.createRow(row++);
            vColHdr1.setHeightInPoints(16);
            String[] vHdr1 = {"Mod", "Gen", "Kvp", "", "Timp scan (s)", "", "DAP ref. (mGy\u00b7cm\u00b2)", "mGy", "mm AI HVL", "\u00b5Gy/s", "pulses", "mm AI TF"};
            for (int i = 0; i < vHdr1.length; i++) {
                Cell c = vColHdr1.createCell(i);
                c.setCellValue(vHdr1[i]);
                c.setCellStyle(hdrStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 2, 3));
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 4, 5));

            Row vColHdr2 = sheet.createRow(row++);
            vColHdr2.setHeightInPoints(16);
            String[] vHdr2 = {"", "", "Referinta", "Masurat", "Referinta", "Masurat", "", "", "", "", "", ""};
            for (int i = 0; i < vHdr2.length; i++) {
                Cell c = vColHdr2.createCell(i);
                c.setCellValue(vHdr2[i]);
                c.setCellStyle(i < 2 || i >= 6 ? hdrStyle : (i % 2 == 0 ? refStyle : hdrStyle));
            }

            boolean altRow = false;
            for (Map.Entry<String, List<String>> entry : MODE_GENDERS.entrySet()) {
                String mode = entry.getKey();
                for (String gender : entry.getValue()) {
                    CbctMeasurementValue v = vals.stream()
                            .filter(x -> x.getMode().equals(mode) && x.getGender().equals(gender))
                            .findFirst().orElse(null);
                    CbctDeviceReference ref = refs.stream()
                            .filter(r -> r.getMode().equals(mode) && r.getGender().equals(gender))
                            .findFirst().orElse(null);
                    CellStyle ds = altRow ? altStyle : dataStyle;
                    altRow = !altRow;
                    Row vRow = sheet.createRow(row++);
                    vRow.setHeightInPoints(15);
                    setCell(vRow, 0, mode, ds);
                    setCell(vRow, 1, gender, ds);
                    setCell(vRow, 2, ref != null ? ref.getKvp() : null, refStyle);
                    setCell(vRow, 3, v != null ? v.getKvp() : null, ds);
                    setCell(vRow, 4, ref != null ? ref.getScanTime() : null, refStyle);
                    setCell(vRow, 5, v != null ? v.getScanTime() : null, ds);
                    setCell(vRow, 6, ref != null ? ref.getDap() : null, refStyle);
                    setCell(vRow, 7, v != null ? v.getMgy() : null, ds);
                    setCell(vRow, 8, v != null ? v.getMmAiHvl() : null, ds);
                    setCell(vRow, 9, v != null ? v.getUGyPerS() : null, ds);
                    setCell(vRow, 10, v != null ? v.getPulses() : null, ds);
                    setCell(vRow, 11, v != null ? v.getMmAiTf() : null, ds);
                }
            }

            // Empty row
            sheet.createRow(row++).setHeightInPoints(8);

            // Dozimetrie section
            Row dozHdr = sheet.createRow(row++);
            dozHdr.setHeightInPoints(18);
            Cell dh = dozHdr.createCell(0);
            dh.setCellValue("DOZIMETRIE");
            dh.setCellStyle(sectionStyle);
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 9));

            Row dozColHdr = sheet.createRow(row++);
            dozColHdr.setHeightInPoints(16);
            Cell dc1 = dozColHdr.createCell(0);
            dc1.setCellValue("Punct masurat");
            dc1.setCellStyle(hdrStyle);
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 3));
            Cell dc2 = dozColHdr.createCell(4);
            dc2.setCellValue("Material perete");
            dc2.setCellStyle(hdrStyle);
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 4, 6));
            Cell dc3 = dozColHdr.createCell(7);
            dc3.setCellValue("Valoarea maxima masurata (\u00b5Sv/h)");
            dc3.setCellStyle(hdrStyle);
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 7, 9));

            altRow = false;
            for (CbctDozimetrie d : doz) {
                CellStyle ds = altRow ? altStyle : dataStyle;
                altRow = !altRow;
                Row dRow = sheet.createRow(row++);
                dRow.setHeightInPoints(15);
                Cell d1 = dRow.createCell(0);
                d1.setCellValue(nullStr(d.getPunctMasurat()));
                d1.setCellStyle(ds);
                sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 3));
                Cell d2 = dRow.createCell(4);
                d2.setCellValue(nullStr(d.getMaterialPerete()));
                d2.setCellStyle(ds);
                sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 4, 6));
                Cell d3 = dRow.createCell(7);
                if (d.getValoareaMaximaMarsurata() != null) d3.setCellValue(d.getValoareaMaximaMarsurata());
                d3.setCellStyle(ds);
                sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 7, 9));
            }

            wb.write(out);
            return excelResponse(out.toByteArray(), "masuratori_cbct_" + id + ".xlsx");
        }
    }

    // ── Individual PDF Export ─────────────────────────────────────────────────

    public ResponseEntity<byte[]> exportMeasurementPdf(Long id) throws Exception {
        CbctMeasurement m = measurementRepository.findById(id).orElseThrow();
        List<CbctMeasurementValue> vals = measurementValueRepository.findByMeasurementId(id);
        List<CbctDozimetrie> doz = dozimetrieRepository.findByMeasurementId(id);
        List<CbctDeviceReference> refs = deviceReferenceRepository.findByDeviceId(m.getDeviceId() != null ? m.getDeviceId() : -1L);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 30, 30, 40, 40);
            PdfWriter writer = PdfWriter.getInstance(doc, out);

            // Page border event
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter w, Document d) {
                    PdfContentByte cb = w.getDirectContent();
                    cb.setColorStroke(new Color(63, 81, 181));
                    cb.setLineWidth(1.5f);
                    cb.rectangle(20, 20, d.getPageSize().getWidth() - 40, d.getPageSize().getHeight() - 40);
                    cb.stroke();
                    // Footer
                    Font ftrFont = new Font(Font.HELVETICA, 8, Font.NORMAL, new Color(120, 120, 120));
                    Phrase footer = new Phrase("Raport CBCT #" + id + "   |   " +
                            (m.getMeasurementDate() != null ? m.getMeasurementDate().format(fmt) : ""), ftrFont);
                    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                            d.getPageSize().getWidth() / 2, 28, 0);
                }
            });

            doc.open();

            Color PRIMARY = new Color(63, 81, 181);
            Color PRIMARY_LIGHT = new Color(232, 234, 246);
            Color REF_BG = new Color(245, 245, 245);
            Color ALT_BG = new Color(250, 251, 255);
            Color WHITE = Color.WHITE;
            Color DARK_TEXT = new Color(33, 33, 33);

            // ── TITLE BLOCK ──
            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);
            PdfPCell titleCell = new PdfPCell();
            titleCell.setBackgroundColor(PRIMARY);
            titleCell.setPadding(14);
            titleCell.setBorder(Rectangle.NO_BORDER);
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, WHITE);
            Font subtitleFont = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(200, 210, 255));
            Paragraph titlePara = new Paragraph();
            titlePara.add(new Chunk("RAPORT MASURATORI CBCT   ", titleFont));
            titlePara.add(new Chunk("#" + id, new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(180, 200, 255))));
            titleCell.addElement(titlePara);
            String customerName = m.getCustomer() != null ? sanitize(m.getCustomer().getName()) : "-";
            String brand = m.getDevice() != null ? sanitize(m.getDevice().getBrand()) : "-";
            String model = m.getDevice() != null ? sanitize(m.getDevice().getModel()) : "-";
            String date = m.getMeasurementDate() != null ? m.getMeasurementDate().format(fmt) : "-";
            titleCell.addElement(new Phrase(customerName + "   |   " + brand + " " + model + "   |   " + date, subtitleFont));
            titleTable.addCell(titleCell);
            doc.add(titleTable);
            doc.add(Chunk.NEWLINE);

            // ── INFO SECTION ──
            doc.add(sectionHeader("INFORMATII GENERALE", PRIMARY));
            PdfPTable infoTable = new PdfPTable(new float[]{1.5f, 3f, 1.5f, 3f});
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(4);
            addInfoCells(infoTable, "Client:", customerName, "Echipament:", brand + " " + model, PRIMARY, PRIMARY_LIGHT);
            addInfoCells(infoTable, "Numar serie:", sanitize(nullStr(m.getSerialNumber())), "Data masuratoare:", date, PRIMARY, PRIMARY_LIGHT);
            doc.add(infoTable);
            doc.add(Chunk.NEWLINE);

            // ── VALUES SECTION ──
            {
                doc.add(sectionHeader("VALORI MASURATE", PRIMARY));
                PdfPTable vTable = new PdfPTable(new float[]{1.2f, 1.3f, 1.1f, 1.1f, 1.2f, 1.2f, 1.3f, 1.3f, 1.2f, 1.2f, 1.2f, 1.2f});
                vTable.setWidthPercentage(100);
                vTable.setSpacingBefore(4);

                Font colHdrFont = new Font(Font.HELVETICA, 7, Font.BOLD, WHITE);
                String[][] colHdrs = {{"Mod"}, {"Gen"}, {"Kvp", "Ref."}, {"Kvp", "Mas."}, {"Timp (s)", "Ref."}, {"Timp (s)", "Mas."}, {"DAP ref.", "(mGy\u00b7cm\u00b2)"}, {"mGy"}, {"mm AI", "HVL"}, {"\u00b5Gy/s"}, {"pulses"}, {"mm AI", "TF"}};
                Color[] colBgs = {PRIMARY, PRIMARY, new Color(100, 120, 200), PRIMARY, new Color(100, 120, 200), PRIMARY, new Color(100, 120, 200), PRIMARY, PRIMARY, PRIMARY, PRIMARY, PRIMARY};
                for (int i = 0; i < colHdrs.length; i++) {
                    PdfPCell hc = new PdfPCell();
                    hc.setBackgroundColor(colBgs[i]);
                    hc.setPadding(4);
                    hc.setHorizontalAlignment(Element.ALIGN_CENTER);
                    hc.setBorderColor(WHITE);
                    Phrase p = new Phrase();
                    p.add(new Chunk(colHdrs[i][0] + (colHdrs[i].length > 1 ? "\n" + colHdrs[i][1] : ""), colHdrFont));
                    hc.addElement(p);
                    vTable.addCell(hc);
                }

                Font dataFont = new Font(Font.HELVETICA, 8, Font.NORMAL, DARK_TEXT);
                Font modeFont = new Font(Font.HELVETICA, 8, Font.BOLD, PRIMARY);
                Font refFont = new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(130, 130, 130));
                boolean altR = false;
                for (Map.Entry<String, List<String>> entry : MODE_GENDERS.entrySet()) {
                    String mode = entry.getKey();
                    for (String gender : entry.getValue()) {
                        CbctMeasurementValue v = vals.stream()
                                .filter(x -> x.getMode().equals(mode) && x.getGender().equals(gender))
                                .findFirst().orElse(null);
                        CbctDeviceReference ref = refs.stream()
                                .filter(r -> r.getMode().equals(mode) && r.getGender().equals(gender))
                                .findFirst().orElse(null);
                        Color bg = altR ? ALT_BG : WHITE;
                        altR = !altR;
                        addVCell(vTable, mode, bg, modeFont, true);
                        addVCell(vTable, gender, bg, dataFont, false);
                        addVCell(vTable, ref != null && ref.getKvp() != null ? fmt(ref.getKvp()) : "—", REF_BG, refFont, false);
                        addVCell(vTable, v != null && v.getKvp() != null ? fmt(v.getKvp()) : "—", bg, dataFont, false);
                        addVCell(vTable, ref != null && ref.getScanTime() != null ? fmt(ref.getScanTime()) : "—", REF_BG, refFont, false);
                        addVCell(vTable, v != null && v.getScanTime() != null ? fmt(v.getScanTime()) : "—", bg, dataFont, false);
                        addVCell(vTable, ref != null && ref.getDap() != null ? fmt(ref.getDap()) : "—", REF_BG, refFont, false);
                        addVCell(vTable, v != null && v.getMgy() != null ? fmt(v.getMgy()) : "—", bg, dataFont, false);
                        addVCell(vTable, v != null && v.getMmAiHvl() != null ? fmt(v.getMmAiHvl()) : "—", bg, dataFont, false);
                        addVCell(vTable, v != null && v.getUGyPerS() != null ? fmt(v.getUGyPerS()) : "—", bg, dataFont, false);
                        addVCell(vTable, v != null && v.getPulses() != null ? fmt(v.getPulses()) : "—", bg, dataFont, false);
                        addVCell(vTable, v != null && v.getMmAiTf() != null ? fmt(v.getMmAiTf()) : "—", bg, dataFont, false);
                    }
                }
                doc.add(vTable);
                doc.add(Chunk.NEWLINE);
            }

            // ── DOZIMETRIE SECTION ──
            if (!doz.isEmpty()) {
                doc.add(sectionHeader("DOZIMETRIE", PRIMARY));
                PdfPTable dTable = new PdfPTable(new float[]{3f, 2f, 2f});
                dTable.setWidthPercentage(90);
                dTable.setHorizontalAlignment(Element.ALIGN_LEFT);
                dTable.setSpacingBefore(4);

                Font dHdrFont = new Font(Font.HELVETICA, 9, Font.BOLD, WHITE);
                PdfPCell dh1 = new PdfPCell(new Phrase("Punct masurat", dHdrFont));
                dh1.setBackgroundColor(PRIMARY); dh1.setPadding(6); dh1.setBorderColor(WHITE);
                PdfPCell dh2 = new PdfPCell(new Phrase("Material perete", dHdrFont));
                dh2.setBackgroundColor(PRIMARY); dh2.setPadding(6); dh2.setBorderColor(WHITE);
                PdfPCell dh3 = new PdfPCell(new Phrase("Valoarea maxima masurata (\u00b5Sv/h)", dHdrFont));
                dh3.setBackgroundColor(PRIMARY); dh3.setPadding(6); dh3.setBorderColor(WHITE);
                dTable.addCell(dh1);
                dTable.addCell(dh2);
                dTable.addCell(dh3);

                Font dDataFont = new Font(Font.HELVETICA, 9, Font.NORMAL, DARK_TEXT);
                boolean altD = false;
                for (CbctDozimetrie d : doz) {
                    Color bg = altD ? ALT_BG : WHITE;
                    altD = !altD;
                    PdfPCell c1 = new PdfPCell(new Phrase(sanitize(nullStr(d.getPunctMasurat())), dDataFont));
                    c1.setPadding(6); c1.setBackgroundColor(bg); c1.setBorderColor(new Color(200, 200, 220));
                    PdfPCell c2 = new PdfPCell(new Phrase(sanitize(nullStr(d.getMaterialPerete())), dDataFont));
                    c2.setPadding(6); c2.setBackgroundColor(bg); c2.setBorderColor(new Color(200, 200, 220));
                    PdfPCell c3 = new PdfPCell(new Phrase(d.getValoareaMaximaMarsurata() != null ? fmt(d.getValoareaMaximaMarsurata()) : "—", dDataFont));
                    c3.setPadding(6); c3.setBackgroundColor(bg); c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    c3.setBorderColor(new Color(200, 200, 220));
                    dTable.addCell(c1);
                    dTable.addCell(c2);
                    dTable.addCell(c3);
                }
                doc.add(dTable);
            }

            doc.close();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=masuratori_cbct_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());
        }
    }

    // ── PDF helpers ───────────────────────────────────────────────────────────

    private Paragraph sectionHeader(String text, Color color) {
        Font f = new Font(Font.HELVETICA, 9, Font.BOLD, color);
        Paragraph p = new Paragraph(text, f);
        p.setSpacingBefore(6);
        p.setSpacingAfter(0);
        return p;
    }

    private void addInfoCells(PdfPTable t, String l1, String v1, String l2, String v2, Color primary, Color lightBg) {
        Font lf = new Font(Font.HELVETICA, 9, Font.BOLD, primary);
        Font vf = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(33, 33, 33));
        PdfPCell lc1 = new PdfPCell(new Phrase(l1, lf));
        lc1.setBackgroundColor(lightBg); lc1.setPadding(5); lc1.setBorderColor(new Color(200, 200, 220));
        PdfPCell vc1 = new PdfPCell(new Phrase(v1, vf));
        vc1.setPadding(5); vc1.setBorderColor(new Color(200, 200, 220));
        PdfPCell lc2 = new PdfPCell(new Phrase(l2, lf));
        lc2.setBackgroundColor(lightBg); lc2.setPadding(5); lc2.setBorderColor(new Color(200, 200, 220));
        PdfPCell vc2 = new PdfPCell(new Phrase(v2, vf));
        vc2.setPadding(5); vc2.setBorderColor(new Color(200, 200, 220));
        t.addCell(lc1); t.addCell(vc1); t.addCell(lc2); t.addCell(vc2);
    }

    private void addVCell(PdfPTable t, String text, Color bg, Font f, boolean bold) {
        Font usedFont = bold ? new Font(Font.HELVETICA, 8, Font.BOLD, f.getColor()) : f;
        PdfPCell c = new PdfPCell(new Phrase(text, usedFont));
        c.setPadding(4); c.setBackgroundColor(bg);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setBorderColor(new Color(210, 210, 230));
        t.addCell(c);
    }

    private String fmt(Double d) {
        if (d == null) return "—";
        if (d == d.longValue()) return String.valueOf(d.longValue());
        return String.format("%.2f", d);
    }

    private String sanitize(String s) {
        if (s == null) return "";
        return s.replace("\u0219", "s").replace("\u0218", "S")
                .replace("\u021B", "t").replace("\u021A", "T")
                .replace("\u0163", "t").replace("\u0162", "T")
                .replace("\u0103", "a").replace("\u0102", "A");
    }

    // ── Excel style helpers ───────────────────────────────────────────────────

    private CellStyle titleStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.LEFT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        org.apache.poi.ss.usermodel.Font f = wb.createFont(); f.setBold(true); f.setColor(IndexedColors.WHITE.getIndex()); f.setFontHeightInPoints((short) 14);
        s.setFont(f);
        return s;
    }

    private CellStyle headerStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
        s.setBottomBorderColor(IndexedColors.WHITE.getIndex());
        org.apache.poi.ss.usermodel.Font f = wb.createFont(); f.setBold(true); f.setColor(IndexedColors.WHITE.getIndex()); f.setFontHeightInPoints((short) 9);
        s.setFont(f);
        return s;
    }

    private CellStyle subHeaderStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        org.apache.poi.ss.usermodel.Font f = wb.createFont(); f.setBold(true); f.setColor(IndexedColors.WHITE.getIndex()); f.setFontHeightInPoints((short) 9);
        s.setFont(f);
        return s;
    }

    private CellStyle sectionHeaderStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.LEFT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        org.apache.poi.ss.usermodel.Font f = wb.createFont(); f.setBold(true); f.setColor(IndexedColors.WHITE.getIndex()); f.setFontHeightInPoints((short) 10);
        s.setFont(f);
        return s;
    }

    private CellStyle infoLabelStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
        org.apache.poi.ss.usermodel.Font f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9);
        s.setFont(f);
        return s;
    }

    private CellStyle infoValueStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
        org.apache.poi.ss.usermodel.Font f = wb.createFont(); f.setFontHeightInPoints((short) 9);
        s.setFont(f);
        return s;
    }

    private CellStyle dataStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
        s.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setAlignment(HorizontalAlignment.CENTER);
        org.apache.poi.ss.usermodel.Font f = wb.createFont(); f.setFontHeightInPoints((short) 9);
        s.setFont(f);
        return s;
    }

    private CellStyle altDataStyle(Workbook wb) {
        CellStyle s = dataStyle(wb);
        s.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return s;
    }

    private CellStyle refStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
        s.setAlignment(HorizontalAlignment.CENTER);
        org.apache.poi.ss.usermodel.Font f = wb.createFont(); f.setItalic(true); f.setFontHeightInPoints((short) 9);
        s.setFont(f);
        return s;
    }

    private int addInfoRow(Sheet sheet, int rowNum, String l1, String v1, String l2, String v2,
                           CellStyle lblStyle, CellStyle valStyle) {
        Row row = sheet.createRow(rowNum);
        row.setHeightInPoints(16);
        Cell c0 = row.createCell(0); c0.setCellValue(l1); c0.setCellStyle(lblStyle);
        Cell c1 = row.createCell(1); c1.setCellValue(v1); c1.setCellStyle(valStyle);
        row.createCell(2).setCellStyle(valStyle); row.createCell(3).setCellStyle(valStyle);
        row.createCell(4).setCellStyle(valStyle);
        Cell c5 = row.createCell(5); c5.setCellValue(l2); c5.setCellStyle(lblStyle);
        Cell c6 = row.createCell(6); c6.setCellValue(v2); c6.setCellStyle(valStyle);
        for (int i = 7; i <= 9; i++) row.createCell(i).setCellStyle(valStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 4));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 6, 9));
        return rowNum + 1;
    }

    private void setCell(Row row, int col, Object val, CellStyle style) {
        Cell c = row.createCell(col);
        if (val instanceof String) c.setCellValue((String) val);
        else if (val instanceof Double) c.setCellValue((Double) val);
        else if (val instanceof Long) c.setCellValue((Long) val);
        else if (val instanceof Number) c.setCellValue(((Number) val).doubleValue());
        c.setCellStyle(style);
    }

    private ResponseEntity<byte[]> excelResponse(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    // ── Private mappers ───────────────────────────────────────────────────────

    private CbctDeviceDto toDeviceDto(CbctDevice d) {
        List<CbctReferenceDto> refs = deviceReferenceRepository.findByDeviceId(d.getId())
                .stream().map(r -> CbctReferenceDto.builder()
                        .id(r.getId()).deviceId(r.getDeviceId())
                        .mode(r.getMode()).gender(r.getGender())
                        .kvp(r.getKvp()).current(r.getCurrent())
                        .scanTime(r.getScanTime()).dap(r.getDap()).build())
                .collect(Collectors.toList());
        return CbctDeviceDto.builder().id(d.getId()).brand(d.getBrand()).model(d.getModel()).references(refs).build();
    }

    private CbctMeasurementDto toMeasurementDto(CbctMeasurement m) {
        List<CbctMeasurementValueDto> vals = measurementValueRepository.findByMeasurementId(m.getId())
                .stream().map(v -> CbctMeasurementValueDto.builder()
                        .id(v.getId()).measurementId(v.getMeasurementId())
                        .mode(v.getMode()).gender(v.getGender())
                        .kvp(v.getKvp()).scanTime(v.getScanTime())
                        .mgy(v.getMgy()).mmAiHvl(v.getMmAiHvl()).uGyPerS(v.getUGyPerS())
                        .pulses(v.getPulses()).mmAiTf(v.getMmAiTf()).build())
                .collect(Collectors.toList());
        List<CbctDozimetrieDto> doz = dozimetrieRepository.findByMeasurementId(m.getId())
                .stream().map(d -> CbctDozimetrieDto.builder()
                        .id(d.getId()).measurementId(d.getMeasurementId())
                        .punctMasurat(d.getPunctMasurat())
                        .valoareaMaximaMarsurata(d.getValoareaMaximaMarsurata())
                        .materialPerete(d.getMaterialPerete()).build())
                .collect(Collectors.toList());
        String customerName = m.getCustomer() != null ? m.getCustomer().getName() : null;
        String deviceBrand = m.getDevice() != null ? m.getDevice().getBrand() : null;
        String deviceModel = m.getDevice() != null ? m.getDevice().getModel() : null;
        return CbctMeasurementDto.builder()
                .id(m.getId()).customerId(m.getCustomerId()).customerName(customerName)
                .deviceId(m.getDeviceId()).deviceBrand(deviceBrand).deviceModel(deviceModel)
                .serialNumber(m.getSerialNumber()).measurementDate(m.getMeasurementDate())
                .values(vals).dozimetrie(doz).build();
    }

    private String nullStr(String s) { return s != null ? s : ""; }
}
