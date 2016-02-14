/**
 * Tim Parsons
 * Copyright 2016
 */
package io.timparsons.betterfaces.renderkit;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.OutputMessageRenderer;

/**
 * @author Tim
 *
 */
public class BetterOutputMessageRenderer extends OutputMessageRenderer {

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component)
        throws IOException {

        rendererParamsNotNull(context, component);

        if (!shouldEncode(component)) {
            return;
        }

        String currentValue = getCurrentValue(context, component);
        // If null, do not putput anything - return.
        if (null == currentValue) {
            return;
        }
        int childCount = component.getChildCount();
        List<Object> parameterList;
        Map<String, Object> parameterMap;

        if (childCount > 0) {
            parameterList = new ArrayList<Object>();
            parameterMap = new HashMap<String, Object>();
            // get UIParameter children...

            for (UIComponent kid : component.getChildren()) {
                // PENDING(rogerk) ignore if child is not UIParameter?
                if (!(kid instanceof UIParameter)) {
                    continue;
                }

                UIParameter param = (UIParameter) kid;

                if (param.getName() != null) {
                    parameterMap.put(param.getName(), param.getValue());
                } else {
                    parameterList.add(param.getValue());
                }
            }
        } else {
            parameterList = Collections.emptyList();
            parameterMap = Collections.emptyMap();
        }

        // If at least one substitution parameter was specified,
        // use the string as a MessageFormat instance.
        String message;
        
        if(!parameterMap.isEmpty()) {
        	for (Entry<String, Object> paramEntry : parameterMap.entrySet()) {
                currentValue = currentValue.replaceAll("\\{" + paramEntry.getKey() + "\\}",
                    String.valueOf(paramEntry.getValue()));
            }
        	
        	if(!parameterList.isEmpty()) {
        		MessageFormat fmt = new MessageFormat(currentValue,
                        context.getViewRoot().getLocale());
                    StringBuffer buf = new StringBuffer(currentValue.length() * 2);
                    fmt.format(parameterList.toArray(new
                        Object[parameterList.size()]),
                        buf,
                        null);
                    message = buf.toString();
        	} else {
        		message = currentValue;
        	}
        } else if (!parameterList.isEmpty()) {
            MessageFormat fmt = new MessageFormat(currentValue,
                context.getViewRoot().getLocale());
            StringBuffer buf = new StringBuffer(currentValue.length() * 2);
            fmt.format(parameterList.toArray(new
                Object[parameterList.size()]),
                buf,
                null);
            message = buf.toString();
        } else {
            message = currentValue;
        }

        ResponseWriter writer = context.getResponseWriter();
        assert (writer != null);

        String style = (String) component.getAttributes().get("style");
        String styleClass = (String) component.getAttributes().get("styleClass");
        String lang = (String) component.getAttributes().get("lang");
        String dir = (String) component.getAttributes().get("dir");
        String title = (String) component.getAttributes().get("title");
        boolean wroteSpan = false;
        if (styleClass != null
            || style != null
            || dir != null
            || lang != null
            || title != null
            || shouldWriteIdAttribute(component)) {
            writer.startElement("span", component);
            writeIdAttributeIfNecessary(context, writer, component);
            wroteSpan = true;

            if (style != null) {
                writer.writeAttribute("style", style, "style");
            }
            if (null != styleClass) {
                writer.writeAttribute("class", styleClass, "styleClass");
            }
            if (dir != null) {
                writer.writeAttribute("dir", dir, "dir");
            }
            if (lang != null) {
                writer.writeAttribute(RenderKitUtils.prefixAttribute("lang", writer),
                    lang,
                    "lang");
            }
            if (title != null) {
                writer.writeAttribute("title", title, "title");
            }
        }

        Object val = component.getAttributes().get("escape");
        boolean escape = (val != null) && Boolean.valueOf(val.toString());

        if (escape) {
            writer.writeText(message, component, "value");
        } else {
            writer.write(message);
        }
        if (wroteSpan) {
            writer.endElement("span");
        }

    }
}
