package github.com.rev;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DeserializationExampleTest {

    private final XMLInputFactory inputFactory = XMLInputFactory.newFactory();

    /**
     * ============================================
     *          XML PARSING TESTS
     * ============================================
     *
     */

    @Test
    public void testXmlParseSingleArtist() throws IOException, XMLStreamException {
        XmlMapper mapper = getXmlMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("artist.xml");

        Artist expected = getExpectedArtistResult();
        Artist actual = mapper.readValue(inputFactory.createXMLStreamReader(is), Artist.class);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Failing test to demonstrate issue, should be passing
     */
    @Test
    public void testXmlParseArtistList() throws IOException, XMLStreamException {
        XmlMapper mapper = getXmlMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("artist_list.xml");

        ArtistList expected = getExpectedArtistListResult();
        ArtistList actual = mapper.readValue(inputFactory.createXMLStreamReader(is), ArtistList.class);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * ============================================
     *          JSON PARSING TESTS
     * ============================================
     *
     * These are analogous to the xml tests, but read from json instead
     */

    @Test
    public void testJsonParseSingleArtist() throws IOException, XMLStreamException {
        ObjectMapper mapper = getJsonMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("artist.json");

        Artist expected = getExpectedArtistResult();
        Artist actual = mapper.readValue(is, Artist.class);

        Assertions.assertEquals(expected, actual);
    }

    /**
     * This works when parsing from json!
     */
    @Test
    public void testJsonParseArtistList() throws IOException, XMLStreamException {
        ObjectMapper mapper = getJsonMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("artist_list.json");


        ArtistList expected = getExpectedArtistListResult();
        ArtistList actual = mapper.readValue(is, ArtistList.class);

        Assertions.assertEquals(expected, actual);
    }

    /**
     * ============================================
     *          WORK AROUND (not ideal)
     * ============================================
     *
     * Parse the xml to json and then convert...
     */
    @Test
    public void testXmlParseArtistListJsonFirst() throws IOException, XMLStreamException {
        XmlMapper mapper = getXmlMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("artist_list.xml");

        JsonNode jsonNode = mapper.readValue(inputFactory.createXMLStreamReader(is), JsonNode.class);

        ArtistList actual = mapper.convertValue(jsonNode, ArtistList.class);
        ArtistList expected = getExpectedArtistListResult();

        Assertions.assertEquals(expected, actual);
    }

    private static Artist getExpectedArtistResult() {
        return getArtist("Fleetwood Mac");
    }
    private static ArtistList getExpectedArtistListResult() {
        ArtistList list = new ArtistList();
        List<Artist> artists = list.getArtist();
        artists.add(getArtist("Fleetwood Mac"));
        artists.add(getArtist("Fleetwood"));
        artists.add(getArtist("Mick Fleetwood"));

        return list;
    }

    private static Artist getArtist(final String name) {
        Artist artist = new Artist();
        artist.setName(name);
        return artist;
    }

    /**
     * I've tried this with the various settings commented out, none of which fix the problem
     */
    private static XmlMapper getXmlMapper() {
        XmlMapper mapper = new XmlMapper(new XmlFactory());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);

//        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//        mapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, false);

        JacksonXmlModule jacksonXmlModule = new JacksonXmlModule();
//        jacksonXmlModule.setDefaultUseWrapper(false);
        mapper.registerModule(jacksonXmlModule);
        return mapper;
    }

    private static ObjectMapper getJsonMapper() {
        return new ObjectMapper();
    }

}
