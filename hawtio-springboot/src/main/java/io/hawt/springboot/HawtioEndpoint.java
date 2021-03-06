package io.hawt.springboot;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.actuate.endpoint.mvc.AbstractNamedMvcEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.hawt.util.Strings;

/**
 * Spring Boot endpoint to expose hawtio.
 */
@ConfigurationProperties(prefix = "endpoints.hawtio", ignoreUnknownFields = false)
public class HawtioEndpoint extends AbstractNamedMvcEndpoint {

    private final String managementContextPrefix;

    private List<HawtPlugin> plugins;

    public HawtioEndpoint(final String managementContextPrefix) {
        super("hawtio", "/hawtio", true);
        this.managementContextPrefix = Strings
                .webContextPath(managementContextPrefix);
    }

    public void setPlugins(final List<HawtPlugin> plugins) {
        this.plugins = plugins;
    }

    @RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
    public String redirect(final HttpServletRequest request) {
        return getIndexHtmlRedirect(request);
    }

    @RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String rootRedirect(final HttpServletRequest request) {
        return getIndexHtmlRedirect(request);
    }

    @RequestMapping("/plugin")
    @ResponseBody
    public List<HawtPlugin> getPlugins() {
        return plugins;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry // @formatter:off
            .addResourceHandler(managementContextPrefix + getPath() + "/plugins/**")
            .addResourceLocations(
                "/app/",
                "classpath:/hawtio-static/app/");
        registry
            .addResourceHandler(managementContextPrefix + getPath() + "/**")
            .addResourceLocations(
                "/",
                "/app/",
                "classpath:/hawtio-static/",
                "classpath:/hawtio-static/app/");
        registry
            .addResourceHandler("/img/**")
            .addResourceLocations("classpath:/hawtio-static/img/"); // @formatter:on
    }

    protected String getIndexHtmlRedirect(final HttpServletRequest request) {
        final ServletUriComponentsBuilder builder = ServletUriComponentsBuilder
                .fromRequest(request);
        final String uriString = builder.build().toUriString();

        return "redirect:" + uriString + (uriString.endsWith("/") ? "" : "/")
                + "index.html";
    }
}
