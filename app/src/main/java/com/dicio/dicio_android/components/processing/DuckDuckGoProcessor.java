package com.dicio.dicio_android.components.processing;

import com.dicio.component.IntermediateProcessor;
import com.dicio.component.standard.StandardResult;
import com.dicio.dicio_android.components.output.SearchOutput;
import com.dicio.dicio_android.util.ConnectionUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class DuckDuckGoProcessor implements IntermediateProcessor<StandardResult, List<SearchOutput.Data>> {

    private static final String duckDuckGoSearchUrl = "https://duckduckgo.com/html/?q=";


    @Override
    public List<SearchOutput.Data> process(StandardResult data) throws Exception {
        final String html = ConnectionUtils.getPage(duckDuckGoSearchUrl
                + ConnectionUtils.urlEncode(data.getCapturingGroup("what").trim()));
        final Document document = Jsoup.parse(html);
        final Elements elements = document.select("div[class=links_main links_deep result__body]");

        final List<SearchOutput.Data> result = new ArrayList<>();
        for (final Element element : elements) {
            final SearchOutput.Data searchResult = new SearchOutput.Data();
            searchResult.title = element.select("a[class=result__a]").first().html();
            searchResult.thumbnailUrl = "https:"
                    + element.select("img[class=result__icon__img]").first().attr("src");
            searchResult.url = element.select("a[class=result__a]").first().attr("href");
            searchResult.url = ConnectionUtils.urlDecode(searchResult.url.substring(15));
            searchResult.description = element.select("a[class=result__snippet]").first().html();

            result.add(searchResult);
        }
        return result;
    }
}