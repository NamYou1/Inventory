package yoyo.inventory.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yoyo.inventory.dto.response.ProductImportResult;
import yoyo.inventory.entities.Category;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.SubCategory;
import yoyo.inventory.entities.Unit;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ApiException;
import yoyo.inventory.repository.CategoryRepository;
import yoyo.inventory.repository.ProductRepository;
import yoyo.inventory.repository.SubCategoryRepository;
import yoyo.inventory.repository.UnitRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductExcelService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final UnitRepository unitRepository;

    // ========================= Column index constants =========================
    private static final int COL_CODE = 0;
    private static final int COL_NAME = 1;
    private static final int COL_OTHER_NAME = 2;
    private static final int COL_SALE_PRICE = 3;
    private static final int COL_COST_PRICE = 4;
    private static final int COL_TAX_METHOD = 5;
    private static final int COL_BARCODE_SYMBOLOGY = 6;
    private static final int COL_TYPE = 7;
    private static final int COL_DETAILS = 8;
    private static final int COL_ALERT_QUANTITY = 9;
    private static final int COL_CATEGORY = 10;
    private static final int COL_SUBCATEGORY = 11;
    private static final int COL_UNIT = 12;

    private static final String[] HEADERS = {
            "Code", "Name", "Other Name", "Sale Price", "Cost Price",
            "Tax Method", "Barcode Symbology", "Type", "Details",
            "Alert Quantity", "Category Name", "SubCategory Name", "Unit Name"
    };

    // =========================================================================
    //  IMPORT
    // =========================================================================

    @Transactional
    public ProductImportResult importFromExcel(MultipartFile file) {
        validateFile(file);

        List<ProductImportResult.RowError> errors = new ArrayList<>();
        int totalRows = 0;
        int successCount = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();

            for (int i = 1; i <= lastRow; i++) { // skip header row
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                totalRows++;
                try {
                    Product product = parseRow(row, i + 1); // i+1 = human-readable row number
                    productRepository.save(product);
                    successCount++;
                } catch (Exception e) {
                    errors.add(new ProductImportResult.RowError(i + 1, e.getMessage()));
                }
            }
        } catch (IOException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Failed to read Excel file: " + e.getMessage());
        }

        return ProductImportResult.builder()
                .totalRows(totalRows)
                .successCount(successCount)
                .failedCount(errors.size())
                .errors(errors)
                .build();
    }

    private Product parseRow(Row row, int rowNum) {
        String code = getStringCell(row, COL_CODE);
        String name = getStringCell(row, COL_NAME);

        if (code == null || code.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Code is required");
        }
        if (name == null || name.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Name is required");
        }

        // Check duplicates in DB
        if (productRepository.findAll().stream().anyMatch(p -> p.getCode().equalsIgnoreCase(code))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product code '" + code + "' already exists");
        }
        if (productRepository.findAll().stream().anyMatch(p -> p.getName().equalsIgnoreCase(name))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product name '" + name + "' already exists");
        }

        // Resolve relations by name
        String categoryName = getStringCell(row, COL_CATEGORY);
        String subCategoryName = getStringCell(row, COL_SUBCATEGORY);
        String unitName = getStringCell(row, COL_UNIT);

        if (categoryName == null || categoryName.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Category Name is required");
        }
        if (subCategoryName == null || subCategoryName.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "SubCategory Name is required");
        }
        if (unitName == null || unitName.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unit Name is required");
        }

        Category category = categoryRepository.findByName(categoryName.trim())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,
                        "Category '" + categoryName + "' not found"));

        SubCategory subCategory = subCategoryRepository.findByName(subCategoryName.trim())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,
                        "SubCategory '" + subCategoryName + "' not found"));

        Unit unit = unitRepository.findByName(unitName.trim())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,
                        "Unit '" + unitName + "' not found"));

        // Build product
        Product product = new Product();
        product.setCode(code.trim());
        product.setName(name.trim());
        product.setOtherName(getStringCell(row, COL_OTHER_NAME));
        product.setSalePrice(getDecimalCell(row, COL_SALE_PRICE));
        product.setCostPrice(getDecimalCell(row, COL_COST_PRICE));
        product.setTaxMethod(getIntegerCell(row, COL_TAX_METHOD));
        product.setBarCodeSymbology(getStringCell(row, COL_BARCODE_SYMBOLOGY));
        product.setType(getStringCell(row, COL_TYPE));
        product.setDetails(getStringCell(row, COL_DETAILS));
        product.setAlertQuantity(getIntegerCell(row, COL_ALERT_QUANTITY) != null
                ? getIntegerCell(row, COL_ALERT_QUANTITY) : 0);
        product.setTblCategory(category);
        product.setTblSubCategory(subCategory);
        product.setTblUnit(unit);
        product.setStatus(Status.ACTIVE);

        return product;
    }

    // =========================================================================
    //  EXPORT
    // =========================================================================

    public byte[] exportToExcel() {
        List<Product> products = productRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Products");

            // ----- Header style -----
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Extend headers to include Status for export
            String[] exportHeaders = {
                    "Code", "Name", "Other Name", "Sale Price", "Cost Price",
                    "Tax Method", "Barcode Symbology", "Type", "Details",
                    "Alert Quantity", "Category Name", "SubCategory Name", "Unit Name", "Status"
            };

            // ----- Write header row -----
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < exportHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(exportHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            // ----- Write data rows -----
            int rowIdx = 1;
            for (Product p : products) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(COL_CODE).setCellValue(safe(p.getCode()));
                row.createCell(COL_NAME).setCellValue(safe(p.getName()));
                row.createCell(COL_OTHER_NAME).setCellValue(safe(p.getOtherName()));
                row.createCell(COL_SALE_PRICE).setCellValue(
                        p.getSalePrice() != null ? p.getSalePrice().doubleValue() : 0);
                row.createCell(COL_COST_PRICE).setCellValue(
                        p.getCostPrice() != null ? p.getCostPrice().doubleValue() : 0);
                row.createCell(COL_TAX_METHOD).setCellValue(
                        p.getTaxMethod() != null ? p.getTaxMethod() : 0);
                row.createCell(COL_BARCODE_SYMBOLOGY).setCellValue(safe(p.getBarCodeSymbology()));
                row.createCell(COL_TYPE).setCellValue(safe(p.getType()));
                row.createCell(COL_DETAILS).setCellValue(safe(p.getDetails()));
                row.createCell(COL_ALERT_QUANTITY).setCellValue(
                        p.getAlertQuantity() != null ? p.getAlertQuantity() : 0);
                row.createCell(COL_CATEGORY).setCellValue(
                        p.getTblCategory() != null ? safe(p.getTblCategory().getName()) : "");
                row.createCell(COL_SUBCATEGORY).setCellValue(
                        p.getTblSubCategory() != null ? safe(p.getTblSubCategory().getName()) : "");
                row.createCell(COL_UNIT).setCellValue(
                        p.getTblUnit() != null ? safe(p.getTblUnit().getName()) : "");
                row.createCell(13).setCellValue(
                        p.getStatus() != null ? p.getStatus().name() : "");
            }

            // Auto-size columns
            for (int i = 0; i < exportHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate Excel file: " + e.getMessage());
        }
    }

    // =========================================================================
    //  Cell helpers
    // =========================================================================

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only .xlsx files are supported");
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getStringCell(Row row, int colIdx) {
        Cell cell = row.getCell(colIdx);
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private BigDecimal getDecimalCell(Row row, int colIdx) {
        Cell cell = row.getCell(colIdx);
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> {
                String val = cell.getStringCellValue().trim();
                yield val.isEmpty() ? null : new BigDecimal(val);
            }
            default -> null;
        };
    }

    private Integer getIntegerCell(Row row, int colIdx) {
        Cell cell = row.getCell(colIdx);
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> {
                String val = cell.getStringCellValue().trim();
                yield val.isEmpty() ? null : Integer.parseInt(val);
            }
            default -> null;
        };
    }

    private String safe(String val) {
        return val != null ? val : "";
    }
}
