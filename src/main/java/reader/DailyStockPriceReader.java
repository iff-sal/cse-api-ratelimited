package reader;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyStockPriceReader implements ExcelReader {

    @Override
    public List<Map<String, Object>> readData(InputStream inputStream) {
        List<Map<String, Object>> data = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(3); // Column headers in 4th row
            int totalRows = sheet.getPhysicalNumberOfRows();

            for (int i = 4; i < totalRows; i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow != null) {
                    Map<String, Object> rowData = new HashMap<>();

                    for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                        String header = headerRow.getCell(j).getStringCellValue().trim();

                        if (currentRow.getCell(j) == null) continue;

                        CellType cellType = currentRow.getCell(j).getCellType();

                        switch (header) {
                            case "TRADING DATE":
                                rowData.put(header, currentRow.getCell(j).getStringCellValue().trim());
                                break;

                            case "PRICE HIGH (Rs.)":
                            case "PRICE LOW (Rs.)":
                            case "CLOSE PRICE (Rs.)":
                            case "OPEN PRICE (Rs.)":
                                rowData.put(header, parseNumericCell(currentRow.getCell(j)));
                                break;

                            default:
                                switch (cellType) {
                                    case STRING:
                                        rowData.put(header, currentRow.getCell(j).getStringCellValue().trim());
                                        break;
                                    case NUMERIC:
                                        rowData.put(header, currentRow.getCell(j).getNumericCellValue());
                                        break;
                                    case BOOLEAN:
                                        rowData.put(header, currentRow.getCell(j).getBooleanCellValue());
                                        break;
                                    default:
                                        // Optionally skip or log unsupported cell types
                                        break;
                                }
                        }
                    }

                    data.add(rowData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    private Object parseNumericCell(org.apache.poi.ss.usermodel.Cell cell) {
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String val = cell.getStringCellValue().trim().replace(",", "");
                if (!val.isEmpty()) {
                    return Double.parseDouble(val);
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse numeric value: " + cell.toString());
        }
        return null;
    }
}
