package reader;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectorMarketCap22Reader implements ExcelReader {
    @Override
    public List<Map<String, Object>> readData(InputStream inputStream) {

        List<Map<String, Object>> data = new ArrayList<>();

        try (Workbook workbook = new HSSFWorkbook(inputStream)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                // header is in the 3rd row
                Row headerRow = sheet.getRow(2);
                if (headerRow == null) {
                    continue;
                }

                // get header names
                List<String> headers = new ArrayList<>();
                for (Cell cell : headerRow) {
                    headers.add(cell.getStringCellValue());
                }

                // data starts from the 4th row
                for (int j = 3; j <= sheet.getLastRowNum(); j++) {
                    Row dataRow = sheet.getRow(j);
                    if (dataRow == null) {
                        continue;
                    }

                    // stop if the first cell  is empty
                    Cell firstCell = dataRow.getCell(0);
                    if (firstCell == null || firstCell.getCellType() == CellType.BLANK) {
                        break;
                    }

                    Map<String, Object> rowData = new HashMap<>();
                    rowData.put("sheetName", sheetName);

                    for (int k = 0; k < headers.size(); k++) {
                        Cell cell = dataRow.getCell(k);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case STRING:
                                    rowData.put(headers.get(k), cell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    rowData.put(headers.get(k), cell.getNumericCellValue());
                                    break;
                                case BLANK:
                                default:
                                    rowData.put(headers.get(k), null);
                                    break;
                            }
                        }
                    }
                    data.add(rowData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //want more robust error handling
        }
        return data;
    }
}
