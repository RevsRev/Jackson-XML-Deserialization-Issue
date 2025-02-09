When using an XmlMapper reading from an input stream, deserialization of objects in a list fails.

Consider the simple case:

```java
public class Artist {
    private String name;
}
public class ArtistList {
    private List<Artist> artists;
}
```

Where we use an XmlMapper to parse the following:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<artist-list>
    <artist>
        <name>Fleetwood Mac</name>
    </artist>
    <artist>
        <name>Fleetwood</name>
    </artist>
    <artist>
        <name>Mick Fleetwood</name>
    </artist>
</artist-list>
```

Specifically:
```java
@Test
public void testXmlParseArtistList() throws IOException, XMLStreamException {
    XmlMapper mapper = getXmlMapper();
    InputStream is = getClass().getClassLoader().getResourceAsStream("artist_list.xml");

    ArtistList expected = getExpectedArtistListResult();
    ArtistList actual = mapper.readValue(inputFactory.createXMLStreamReader(is), ArtistList.class);
    Assertions.assertEquals(expected, actual);
}
```

An exception is thrown when using the mapper to read from the input stream:

```
com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of `github.com.rev.Artist` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('Fleetwood Mac')
 at [Source: (com.ctc.wstx.sr.ValidatingStreamReader); line: 4, column: 28] (through reference chain: github.com.rev.ArtistList["artist"]->java.util.ArrayList[0])

	at com.fasterxml.jackson.databind.exc.MismatchedInputException.from(MismatchedInputException.java:63)
	at com.fasterxml.jackson.databind.DeserializationContext.reportInputMismatch(DeserializationContext.java:1754)
	at com.fasterxml.jackson.databind.DeserializationContext.handleMissingInstantiator(DeserializationContext.java:1379)
	at com.fasterxml.jackson.databind.deser.std.StdDeserializer._deserializeFromString(StdDeserializer.java:311)
	at com.fasterxml.jackson.databind.deser.BeanDeserializerBase.deserializeFromString(BeanDeserializerBase.java:1592)
	at com.fasterxml.jackson.databind.deser.BeanDeserializer._deserializeOther(BeanDeserializer.java:197)
	at com.fasterxml.jackson.databind.deser.BeanDeserializer.deserialize(BeanDeserializer.java:187)
	at com.fasterxml.jackson.databind.deser.std.CollectionDeserializer._deserializeFromArray(CollectionDeserializer.java:361)
	at com.fasterxml.jackson.databind.deser.std.CollectionDeserializer.deserialize(CollectionDeserializer.java:274)
	at com.fasterxml.jackson.databind.deser.std.CollectionDeserializer.deserialize(CollectionDeserializer.java:30)
	at com.fasterxml.jackson.databind.deser.impl.SetterlessProperty.deserializeAndSet(SetterlessProperty.java:134)
	at com.fasterxml.jackson.databind.deser.BeanDeserializer.vanillaDeserialize(BeanDeserializer.java:310)
	at com.fasterxml.jackson.databind.deser.BeanDeserializer.deserialize(BeanDeserializer.java:177)
	at com.fasterxml.jackson.dataformat.xml.deser.XmlDeserializationContext.readRootValue(XmlDeserializationContext.java:104)
	at com.fasterxml.jackson.databind.ObjectMapper._readValue(ObjectMapper.java:4893)
	at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3105)
	at com.fasterxml.jackson.dataformat.xml.XmlMapper.readValue(XmlMapper.java:404)
	at com.fasterxml.jackson.dataformat.xml.XmlMapper.readValue(XmlMapper.java:379)
	at github.com.rev.DeserializationExampleTest.testXmlParseArtistList(DeserializationExampleTest.java:48)
	at java.base/java.lang.reflect.Method.invoke(Method.java:577)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
```

Jackson is trying to construct each Artist using a constructor that takes a String value, but no such constructor exists. It should still be able to parse the result by using the default constructor (which does exist) and then setting the field value appropriately.

This is **not** just a simple case of "Well, use a constructor with a String argument instead", because the underlying issue is that the xml parser is incorrectly reading values in the ArtistList. Specifically, it is trying to construct each `Artist` from the fields contained _within_ `<Artist>`. That is to say, for each field within the `<Artist>` tag, Jackson is trying to add a new `Artist` for _each_ one, which is incorrect (although this example only has one field: `name`).

When parsing an InputStream containing JSON data, Jackson works correctly:

```json
{
  "artist": [
    {
      "name": "Fleetwood Mac"
    },
    {
      "name": "Fleetwood"
    },
    {
      "name": "Mick Fleetwood"
    }
  ]
}
```

The following test passes:

```java
@Test
    public void testJsonParseArtistList() throws IOException, XMLStreamException {
        ObjectMapper mapper = getJsonMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("artist_list.json");


        ArtistList expected = getExpectedArtistListResult();
        ArtistList actual = mapper.readValue(is, ArtistList.class);

        Assertions.assertEquals(expected, actual);
    }
```