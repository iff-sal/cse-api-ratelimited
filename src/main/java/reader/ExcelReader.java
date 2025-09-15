package reader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * each implementation will be responsibel for specif types of file format(data products
 */
public interface ExcelReader {
    /**
     *
     * @param inputStream
     * @return a list of maps, where each map is row of data and the map keys are column name and values are respective data in row
     */
    List<Map<String, Object>> readData(InputStream inputStream);
}
