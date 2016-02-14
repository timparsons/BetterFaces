/**
 * Tim Parsons
 * Copyright 2016
 */
package io.timparsons.betterfaces.renderkit;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.HtmlBasicRenderer;
import com.sun.faces.renderkit.html_basic.MenuRenderer;
import com.sun.faces.util.RequestStateManager;
import com.sun.faces.util.Util;

/**
 * @author Tim
 *
 */
public abstract class AbstractBetterSelectRenderer extends MenuRenderer {

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component)
        throws IOException {

        rendererParamsNotNull(context, component);

        if (!shouldEncode(component)) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        assert (writer != null);

        boolean wrapLabel = false;
        String wrapLabelStr = (String) component.getAttributes().get("wrapLabel");
        String wrapLabelClass = (String) component.getAttributes().get("labelWrapClass");
        String wrapHtml = (String) component.getAttributes().get("wrapHtml");
        String wrapHtmlClass = (String) component.getAttributes().get("htmlWrapClass");
        String labelClass = (String) component.getAttributes().get("labelClass");
        if (wrapLabelStr != null) {
            wrapLabel = "true".equalsIgnoreCase(wrapLabelStr);
        }

        Converter converter = null;
        if (component instanceof ValueHolder) {
            converter = ((ValueHolder) component).getConverter();
        }

        Iterator<SelectItem> items =
            RenderKitUtils.getSelectItems(context, component);

        Object currentSelections = getCurrentSelectedValues(component);
        Object[] submittedValues = getSubmittedSelectedValues(component);
        Map<String, Object> attributes = component.getAttributes();
        OptionComponentInfo optionInfo =
            new OptionComponentInfo((String) attributes.get("disabledClass"),
                (String) attributes.get("enabledClass"),
                (String) attributes.get("unselectedClass"),
                (String) attributes.get("selectedClass"),
                Util.componentIsDisabled(component),
                isHideNoSelection(component));
        int idx = -1;
        while (items.hasNext()) {
            SelectItem curItem = items.next();
            idx++;
            // If we come across a group of options, render them as a nested
            // table.
            if (curItem instanceof SelectItemGroup) {
                // render options of this group.
                SelectItem[] itemsArray =
                    ((SelectItemGroup) curItem).getSelectItems();
                for (int i = 0; i < itemsArray.length; ++i) {
                    renderOption(context,
                        component,
                        converter,
                        itemsArray[i],
                        currentSelections,
                        submittedValues,
                        wrapLabel,
                        labelClass,
                        idx++,
                        optionInfo,
                        wrapHtml,
                        wrapHtmlClass,
                        wrapLabelClass);
                }
            } else {
                renderOption(context,
                    component,
                    converter,
                    curItem,
                    currentSelections,
                    submittedValues,
                    wrapLabel,
                    labelClass,
                    idx,
                    optionInfo,
                    wrapHtml,
                    wrapHtmlClass,
                    wrapLabelClass);
            }
        }

    }

    // ------------------------------------------------------- Protected Methods

    /**
     * We override isBehaviorSource since the ID of the activated check box will
     * have been augmented with the option number.
     *
     * @see HtmlBasicRenderer#isBehaviorSource(FacesContext, String, String)
     */
    @Override
    protected boolean isBehaviorSource(final FacesContext ctx,
        final String behaviorSourceId,
        final String componentClientId) {

        if (behaviorSourceId == null) {
            return false;
        }
        char sepChar = UINamingContainer.getSeparatorChar(ctx);
        String actualBehaviorId;
        if (behaviorSourceId.lastIndexOf(sepChar) != -1) {
            actualBehaviorId = behaviorSourceId.substring(0, behaviorSourceId.lastIndexOf(sepChar));
        } else {
            actualBehaviorId = behaviorSourceId;
        }

        return (actualBehaviorId.equals(componentClientId));

    }

    abstract protected boolean shouldReturn(final Object currentSelections, final Object curValue);

    abstract protected void writeInput(ResponseWriter writer, UIComponent component, boolean checked,
        FacesContext context, String idString, SelectItem curItem, Converter converter, OptionComponentInfo optionInfo)
        throws ConverterException, IOException;

    protected void renderOption(final FacesContext context,
        final UIComponent component,
        final Converter converter,
        final SelectItem curItem,
        final Object currentSelections,
        final Object[] submittedValues,
        final boolean wrapLabel,
        final String userLabelClass, final int itemNumber,
        final OptionComponentInfo optionInfo,
        final String wrapHtml,
        final String wrapHtmlClass,
        final String wrapLabelClass) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        assert (writer != null);

        RequestStateManager.set(context,
            RequestStateManager.TARGET_COMPONENT_ATTRIBUTE_NAME,
            component);

        Object itemValue = getItemValue(context, component, curItem.getValue(), converter, submittedValues);

        boolean isChecked = getIsChecked(context, component, curItem.getValue(), submittedValues, currentSelections,
            itemValue, converter);

        if (optionInfo.isHideNoSelection()
            && curItem.isNoSelectionOption()
            && !isChecked
            && shouldReturn(currentSelections, itemValue)) {
            return;
        }

        StringBuilder labelClass = new StringBuilder();
        if (userLabelClass != null) {
            labelClass.append(userLabelClass);
        }

        if (optionInfo.isDisabled() || curItem.isDisabled()) {
            if (optionInfo.getDisabledClass() != null) {
                labelClass.append(" ").append(optionInfo.getDisabledClass());
            }
        } else {
            if (optionInfo.getEnabledClass() != null) {
                labelClass.append(" ").append(optionInfo.getEnabledClass());
            }
        }

        if (wrapHtml != null) {
            writer.startElement(wrapHtml, component);

            if (wrapHtmlClass != null) {
                writer.writeAttribute("class", wrapHtmlClass, "wrapHtmlClass");
            }
        }

        String idString = component.getClientId(context)
            + UINamingContainer.getSeparatorChar(context)
            + Integer.toString(itemNumber);

        if (wrapLabel) {
            writer.startElement("label", component);
            writer.writeAttribute("for", idString, "for");
            // if enabledClass or disabledClass attributes are specified, apply
            // it on the label.
            if (labelClass != null && labelClass.length() > 0) {
                writer.writeAttribute("class", labelClass, "labelClass");
            }
        }

        writeInput(writer, component, isChecked, context, idString, curItem, converter, optionInfo);

        if (wrapLabel) {
            String itemLabel = curItem.getLabel();
            if (itemLabel != null) {
                writer.startElement("span", component);
                if (wrapLabelClass != null) {
                    writer.writeAttribute("class", wrapLabelClass, "wrapLabelClass");
                }
                writer.writeText(itemLabel, component, "label");
                writer.endElement("span");
            }
            writer.endElement("label");
        } else {
            writer.startElement("label", component);
            writer.writeAttribute("for", idString, "for");
            // if enabledClass or disabledClass attributes are specified, apply
            // it on the label.
            if (labelClass != null) {
                writer.writeAttribute("class", labelClass, "labelClass");
            }
            String itemLabel = curItem.getLabel();
            if (itemLabel != null) {
                writer.writeText(" ", component, null);
                if (!curItem.isEscape()) {
                    // It seems the ResponseWriter API should
                    // have a writeText() with a boolean property
                    // to determine if it content written should
                    // be escaped or not.
                    writer.write(itemLabel);
                } else {
                    writer.writeText(itemLabel, component, "label");
                }
            }
            writer.endElement("label");
        }

        if (wrapHtml != null) {
            writer.endElement(wrapHtml);
        }

        writer.writeText("\n", component, null);
    }

    abstract protected boolean getIsChecked(final FacesContext context, final UIComponent component,
        final Object value, Object[] submittedValues, Object currentSelections, Object itemValue,
        final Converter converter);

    abstract protected Object getItemValue(FacesContext context, UIComponent component, Object value,
        Converter converter, Object[] submittedValues);
}
