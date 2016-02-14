/**
 * Tim Parsons
 * Copyright 2016
 */
package io.timparsons.betterfaces.renderkit;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;

/**
 * @author Tim
 *
 */
public class BetterSelectManyCheckboxListRenderer extends AbstractBetterSelectRenderer {

    private static final Attribute[] ATTRIBUTES =
        AttributeManager.getAttributes(AttributeManager.Key.SELECTMANYCHECKBOX);

    @Override
    protected void writeInput(final ResponseWriter writer, final UIComponent component, final boolean checked,
        final FacesContext context,
        final String idString, final SelectItem curItem, final Converter converter, final OptionComponentInfo optionInfo)
        throws ConverterException, IOException {

        writer.startElement("input", component);
        writer.writeAttribute("name", component.getClientId(context),
            "clientId");
        writer.writeAttribute("id", idString, "id");

        writer.writeAttribute("value", getFormattedValue(context, component,
            curItem.getValue(), converter), "value");
        writer.writeAttribute("type", "checkbox", null);

        if (checked) {
            writer.writeAttribute("checked", Boolean.TRUE, null);
        }

        // Don't render the disabled attribute twice if the 'parent'
        // component is already marked disabled.
        if (!optionInfo.isDisabled()) {
            if (curItem.isDisabled()) {
                writer.writeAttribute("disabled", true, "disabled");
            }
        }

        // Apply HTML 4.x attributes specified on UISelectMany component to all
        // items in the list except styleClass and style which are rendered as
        // attributes of outer most table.
        RenderKitUtils.renderPassThruAttributes(context,
            writer,
            component,
            ATTRIBUTES,
            getNonOnClickSelectBehaviors(component));

        RenderKitUtils.renderXHTMLStyleBooleanAttributes(writer, component);

        RenderKitUtils.renderSelectOnclick(context, component, false);

        writer.endElement("input");
    }

    @Override
    protected boolean shouldReturn(final Object currentSelections, final Object curValue) {
        return currentSelections != null;
    }

    @Override
    protected Object getItemValue(final FacesContext context, final UIComponent component, final Object value,
        final Converter converter, final Object[] submittedValues) {

        String valueString = getFormattedValue(context, component,
            value, converter);

        Object itemValue;
        if (submittedValues != null) {
            itemValue = valueString;
        } else {
            itemValue = value;
        }

        return itemValue;
    }

    @Override
    protected boolean getIsChecked(final FacesContext context, final UIComponent component,
        final Object value, final Object[] submittedValues, final Object currentSelections, final Object itemValue,
        final Converter converter) {

        String valueString = getFormattedValue(context, component,
            value, converter);

        Object valuesArray;
        if (submittedValues != null) {
            valuesArray = submittedValues;
        } else {
            valuesArray = currentSelections;
        }

        return isSelected(context, component, itemValue, valuesArray, converter);
    }

}
