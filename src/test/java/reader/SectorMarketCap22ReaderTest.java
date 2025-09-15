package reader;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SectorMarketCap22ReaderTest {
    @Test
    public void testReadData() {
        InputStream inputStream = this.getClass().getResourceAsStream("/22Sector Market Capitalisation.xls");
        assertNotNull(inputStream, "Input file not found in resources");

        SectorMarketCap22Reader reader = new SectorMarketCap22Reader();
        List<Map<String, Object>> data = reader.readData(inputStream);
        assertNotNull(data, "Data should not be null");

        if(!data.isEmpty()) {
            System.out.println(data.get(0));
        }

    }
}
