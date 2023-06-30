package cn.ares.boot.util.common;


import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author: Ares
 * @time: 2022-04-12 10:33:12
 * @description: Xml util
 * @version: JDK 1.8
 */
public class XmlUtil {

  public static final String INVALID_REGEX = "[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]";

  private static final String DEFAULT_DOCUMENT_BUILDER_FACTORY = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";

  private static boolean namespaceAware = true;

  synchronized public static void setNamespaceAware(boolean isNamespaceAware) {
    namespaceAware = isNamespaceAware;
  }

  /**
   * @author: Ares
   * @description: XML格式字符串转换为Map
   * @description: XML format string converted to Map
   * @time: 2022-04-12 10:35:23
   * @params: [xmlStr] XML格式字符串
   * @return: java.util.Map<java.lang.String, java.lang.Object>
   */
  public static Map<String, Object> xmlToMap(String xmlStr) {
    return xmlToMap(xmlStr, new HashMap<>());
  }

  /**
   * @author: Ares
   * @description: 映射转换为XML文档
   * @description: Map to XML document
   * @time: 2022-06-08 14:22:36
   * @params: [data, rootName] 数据，根名
   * @return: org.w3c.dom.Document
   */
  public static Document mapToXml(Map<?, ?> data, String rootName) {
    return mapToXml(data, rootName, null);
  }

  /**
   * @author: Ares
   * @description: 以指定命名空间将Map转换为XML文档
   * @description: Convert Map to XML document with specified namespace
   * @time: 2022-06-08 14:22:36
   * @params: [data, rootName, namespace] 数据，根名，命名空间
   * @return: org.w3c.dom.Document
   */
  public static Document mapToXml(Map<?, ?> data, String rootName, String namespace) {
    final Document doc = createXml();
    final Element root = appendChild(doc, rootName, namespace);

    appendMap(doc, root, data);
    return doc;
  }

  /**
   * @author: Ares
   * @description: 节点追加子元素
   * @description: Node append child element
   * @time: 2022-06-08 14:24:15
   * @params: [node, tagName, namespace] 节点，标签名，命名空间
   * @return: org.w3c.dom.Element 子元素
   * @return: org.w3c.dom.Element child Element
   */
  public static Element appendChild(Node node, String tagName, String namespace) {
    final Document doc = getOwnerDocument(node);
    final Element child =
        (null == namespace) ? doc.createElement(tagName) : doc.createElementNS(namespace, tagName);
    node.appendChild(child);
    return child;
  }

  /**
   * @author: Ares
   * @description: 获取节点的xml文档
   * @description: Get the xml document of the node
   * @time: 2022-06-08 14:25:38
   * @params: [node] 节点
   * @return: org.w3c.dom.Document
   */
  public static Document getOwnerDocument(Node node) {
    return (node instanceof Document) ? (Document) node : node.getOwnerDocument();
  }

  private static void appendMap(Document doc, Node node, Map<?, ?> data) {
    data.forEach((key, value) -> {
      if (null != key) {
        final Element child = appendChild(node, key.toString());
        if (null != value) {
          append(doc, child, value);
        }
      }
    });
  }

  private static void append(Document doc, Node node, Object data) {
    if (data instanceof Map) {
      // 如果值为map，递归继续
      // If the value is map, the recursion continues
      appendMap(doc, node, (Map<?, ?>) data);
    } else if (data instanceof Iterator) {
      // 如果值为迭代器，递归继续
      // If the value is Iterator, the recursion continues
      appendIterator(doc, node, (Iterator<?>) data);
    } else if (data instanceof Iterable) {
      // 如果值是可迭代的，递归继续
      // If the value is Iterable, the recursion continues
      appendIterator(doc, node, ((Iterable<?>) data).iterator());
    } else {
      appendText(doc, node, data.toString());
    }
  }

  private static void appendIterator(Document doc, Node node, Iterator<?> data) {
    final Node parentNode = node.getParentNode();
    boolean isFirst = true;
    Object eleData;
    while (data.hasNext()) {
      eleData = data.next();
      if (isFirst) {
        append(doc, node, eleData);
        isFirst = false;
      } else {
        final Node cloneNode = node.cloneNode(false);
        parentNode.appendChild(cloneNode);
        append(doc, cloneNode, eleData);
      }
    }
  }

  private static Node appendText(Document doc, Node node, CharSequence text) {
    return node.appendChild(doc.createTextNode(StringUtil.str(text)));
  }

  /**
   * @author: Ares
   * @description: 节点追加子元素
   * @description: Node append child element
   * @time: 2022-06-08 14:24:15
   * @params: [node, tagName] 节点，标签名
   * @return: org.w3c.dom.Element 子元素
   * @return: org.w3c.dom.Element child Element
   */
  public static Element appendChild(Node node, String tagName) {
    return appendChild(node, tagName, null);
  }

  /**
   * @author: Ares
   * @description: Parse xml string into Map
   * @description: 解析xml字符串到Map中
   * @time: 2022-06-08 14:27:49
   * @params: [xmlStr, result] xml字符串, 结果Map
   * @return: java.util.Map<java.lang.String,java.lang.Object>
   */
  public static Map<String, Object> xmlToMap(String xmlStr, Map<String, Object> result) {
    final Document doc = parseXml(xmlStr);
    final Element root = getRootElement(doc);
    root.normalize();

    return xmlToMap(root, result);
  }

  /**
   * @author: Ares
   * @description: 解析节点到Map中
   * @description: Parse node into Map
   * @time: 2022-06-08 14:27:49
   * @params: [node, result] 节点, 结果Map
   * @return: java.util.Map<java.lang.String,java.lang.Object>
   */
  public static Map<String, Object> xmlToMap(Node node, Map<String, Object> result) {
    if (null == result) {
      result = new HashMap<>(16);
    }
    final NodeList nodeList = node.getChildNodes();
    final int length = nodeList.getLength();
    Node childNode;
    Element childEle;
    for (int i = 0; i < length; ++i) {
      childNode = nodeList.item(i);
      if (!isElement(childNode)) {
        continue;
      }

      childEle = (Element) childNode;
      final Object value = result.get(childEle.getNodeName());
      Object newValue;
      if (childEle.hasChildNodes()) {
        // 子节点继续递归遍历
        final Map<String, Object> map = xmlToMap(childEle);
        if (MapUtil.isNotEmpty(map)) {
          newValue = map;
        } else {
          newValue = childEle.getTextContent();
        }
      } else {
        newValue = childEle.getTextContent();
      }

      if (null != newValue) {
        if (null != value) {
          if (value instanceof List) {
            ((List<Object>) value).add(newValue);
          } else {
            result.put(childEle.getNodeName(), CollectionUtil.newArrayList(value, newValue));
          }
        } else {
          result.put(childEle.getNodeName(), newValue);
        }
      }
    }
    return result;
  }

  /**
   * @author: Ares
   * @description: 解析xml字符串返回Map
   * @description: Parse the xml string and return a Map
   * @time: 2022-06-08 14:27:49
   * @params: [xmlStr] xml字符串
   * @return: java.util.Map<java.lang.String,java.lang.Object>
   */
  public static Map<String, Object> xmlToMap(Node node) {
    return xmlToMap(node, new HashMap<>(16));
  }

  /**
   * @author: Ares
   * @description: 将xml格式字符串转换为xml文档
   * @description: Convert xml format string to xml document
   * @time: 2022-04-12 10:36:00
   * @params: [xmlStr] xml格式字符串
   * @return: org.w3c.dom.Document
   */
  public static Document parseXml(String xmlStr) {
    if (StringUtil.isBlank(xmlStr)) {
      throw new IllegalArgumentException("Xml content string is empty");
    }
    xmlStr = cleanInvalid(xmlStr);
    return readXml(StringUtil.getReader(xmlStr));
  }

  /**
   * @author: Ares
   * @description: Convert reader to xml document
   * @time: 2022-06-08 14:37:10
   * @params: [reader]
   * @return: org.w3c.dom.Document
   */
  public static Document readXml(Reader reader) {
    return readXml(new InputSource(reader));
  }

  /**
   * @author: Ares
   * @description: Convert input source to xml document
   * @time: 2022-06-08 14:37:10
   * @params: [source]
   * @return: org.w3c.dom.Document
   */
  public static Document readXml(InputSource source) {
    final DocumentBuilder builder = createDocumentBuilder();
    try {
      return builder.parse(source);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @author: Ares
   * @description: create document builder
   * @time: 2022-06-08 14:37:44
   * @params: []
   * @return: javax.xml.parsers.DocumentBuilder
   */
  public static DocumentBuilder createDocumentBuilder() {
    DocumentBuilder builder;
    try {
      builder = createDocumentBuilderFactory().newDocumentBuilder();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return builder;
  }

  /**
   * @author: Ares
   * @description: create document builder factory
   * @time: 2022-06-08 14:38:16
   * @params: []
   * @return: javax.xml.parsers.DocumentBuilderFactory
   */
  public static DocumentBuilderFactory createDocumentBuilderFactory() {
    final DocumentBuilderFactory factory;
    if (StringUtil.isNotEmpty(DEFAULT_DOCUMENT_BUILDER_FACTORY)) {
      factory = DocumentBuilderFactory.newInstance(DEFAULT_DOCUMENT_BUILDER_FACTORY, null);
    } else {
      factory = DocumentBuilderFactory.newInstance();
    }
    // 默认打开NamespaceAware，getElementsByTagNameNS可以使用命名空间
    factory.setNamespaceAware(namespaceAware);
    return disableXXE(factory);
  }

  /**
   * @author: Ares
   * @description: 关闭XXE，避免漏洞攻击
   * @description: Turn off XXE to avoid exploits
   * @time: 2022-04-12 10:48:01
   * @params: [documentBuilderFactory]
   * @return: javax.xml.parsers.DocumentBuilderFactory
   */
  private static DocumentBuilderFactory disableXXE(DocumentBuilderFactory documentBuilderFactory) {
    String feature;
    try {
      // This is the PRIMARY defense. If DTDs (doctype) are disallowed, almost all XML entity attacks are prevented
      // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
      feature = "http://apache.org/xml/features/disallow-doctype-decl";
      documentBuilderFactory.setFeature(feature, true);
      // If you can't completely disable DTDs, then at least do the following:
      // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
      // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
      // JDK7+ - http://xml.org/sax/features/external-general-entities
      feature = "http://xml.org/sax/features/external-general-entities";
      documentBuilderFactory.setFeature(feature, false);
      // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
      // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
      // JDK7+ - http://xml.org/sax/features/external-parameter-entities
      feature = "http://xml.org/sax/features/external-parameter-entities";
      documentBuilderFactory.setFeature(feature, false);
      // Disable external DTDs as well
      feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
      documentBuilderFactory.setFeature(feature, false);
      // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
      documentBuilderFactory.setXIncludeAware(false);
      documentBuilderFactory.setExpandEntityReferences(false);
    } catch (ParserConfigurationException e) {
      // ignore
    }
    return documentBuilderFactory;
  }

  /**
   * @author: Ares
   * @description: 去除xml文本中的无效字符
   * @description: Remove invalid characters from xml text
   * @time: 2022-04-12 10:36:37
   * @params: [xmlContent] xml文本
   * @return: java.lang.String 去除后xml文本
   */
  public static String cleanInvalid(String xmlContent) {
    if (null == xmlContent) {
      return null;
    }
    return xmlContent.replaceAll(INVALID_REGEX, "");
  }

  /**
   * @author: Ares
   * @description: Get root element
   * @time: 2022-06-08 14:39:46
   * @params: [doc] 文档
   * @return: org.w3c.dom.Element 根元素
   */
  public static Element getRootElement(Document doc) {
    return (null == doc) ? null : doc.getDocumentElement();
  }

  /**
   * @author: Ares
   * @description: 节点是否是元素
   * @description: Whether the node is an element
   * @time: 2022-06-08 14:40:24
   * @params: [node] 节点
   * @return: boolean 是否是元素
   */
  public static boolean isElement(Node node) {
    return (null != node) && Node.ELEMENT_NODE == node.getNodeType();
  }

  /**
   * @author: Ares
   * @description: create xml document
   * @time: 2022-06-08 14:41:01
   * @params: []
   * @return: org.w3c.dom.Document
   */
  public static Document createXml() {
    return createDocumentBuilder().newDocument();
  }
}
