package io.jyasonx.jsxpay.util.xstream;

import com.google.common.base.Strings;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.*;

/**
 * The converter for parsing XML as a nested map.
 */
public class NestedMapConverter implements Converter {
  private final OrdinalType ordinalType;

  public NestedMapConverter() {
    this.ordinalType = OrdinalType.UNORDERED;
  }

  public NestedMapConverter(OrdinalType ordinalType) {
    this.ordinalType = ordinalType;
  }

  /**
   * Determines whether the converter can marshall a particular type.
   *
   * @param type the Class representing the object type to be converted
   */
  @Override
  public boolean canConvert(Class type) {
    return AbstractMap.class.isAssignableFrom(type);
  }

  /**
   * Convert an object to textual data.
   *
   * @param source  The object to be marshaled.
   * @param writer  A stream to write to.
   * @param context A context that allows nested objects to be processed by XStream.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
    AbstractMap<String, String> map = (AbstractMap<String, String>) source;
    map.forEach((key, value) -> {
      writer.startNode(key);
      writer.setValue(value);
      writer.endNode();
    });
  }

  /**
   * Convert textual data back into an object.
   *
   * @param reader  The stream to read the text from.
   * @param context A context that allows reader's content to be processed by XStream.
   * @return The resulting object.
   */
  @Override
  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    return nested(reader);
  }

  @SuppressWarnings("unchecked")
  private Map nested(HierarchicalStreamReader reader) {
    Map map;
    if (ordinalType.equals(OrdinalType.ASCII)) {
      map = new TreeMap();
    } else if (ordinalType.equals(OrdinalType.LINKED)) {
      map = new LinkedHashMap();
    } else {
      map = new HashMap();
    }

    while (reader.hasMoreChildren()) {
      reader.moveDown();

      if (reader.hasMoreChildren()) {
        map.put(reader.getNodeName(), nested(reader));
      } else {
        if (!Strings.isNullOrEmpty(reader.getValue())) {
          map.put(reader.getNodeName(), reader.getValue());
        }
      }

      reader.moveUp();
    }

    return map;
  }

}