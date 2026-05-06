package io.muzoo.ssc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.text.html.HTML;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HtmlLinkExtractor implements LinkExtractor{

    public record TagAttribute(String tag, String attribute){
    }

    private static final List<TagAttribute> DEFAULT_PAIRS = List.of(
            new TagAttribute("a", "href"),
            new TagAttribute("img", "src"),
            new TagAttribute("link", "href"),
            new TagAttribute("script", "src"),
            new TagAttribute("iframe", "src")
    );

    private final List<TagAttribute> tagAttributes;

    public HtmlLinkExtractor(){
        this(DEFAULT_PAIRS);
    }

    public HtmlLinkExtractor(List<TagAttribute> tagAttributes){
        this.tagAttributes = List.copyOf(tagAttributes);
    }

    @Override
    public Set<String> extractLinks(String html){
        Document document = Jsoup.parse(html);
        Set<String> links = new LinkedHashSet<>();
        for (TagAttribute pair : tagAttributes){
            for (Element element : document.select(pair.tag())){
                String value = element.attr(pair.attribute());
                if (!value.isEmpty()){
                    links.add(value);
                }
            }
        }
        return links;
    }

}
