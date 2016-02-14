/**
 * Tim Parsons
 * Copyright 2016
 */
package io.timparsons.betterfaces.renderkit;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.el.ELException;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.HtmlBasicRenderer.OptionComponentInfo;

/**
 * @author Tim
 *
 */
public class BetterRadioRenderer extends AbstractBetterSelectRenderer {

    private static final Attribute[] ATTRIBUTES =
        AttributeManager.getAttributes(AttributeManager.Key.SELECTONERADIO);

    @Override
    protected void writeInput(final ResponseWriter writer, final UIComponent component, final boolean checked,
        final FacesContext context, final String idString, final SelectItem curItem, final Converter converter,
        final OptionComponentInfo optionInfo) throws ConverterException, IOException {
        writer.startElement("input", component);
        writer.writeAttribute("type", "radio", "type");

        if (checked) {
            writer.writeAttribute("checked", Boolean.TRUE, null);
            }
        writer.writeAttribute("name", component.getClientId(context),
            "clientId");
        writer.writeAttribute("id", idString, "id");

        writer.writeAttribute("value",
            (getFormattedValue(context, component,
                curItem.getValue(), converter)),
            "value");

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
        RenderKitUtils.renderXHTMLStyleBooleanAttributes(writer,
            component);

        RenderKitUtils.renderSelectOnclick(context, component, false);

        writer.endElement("input");
    }

    @Override
    protected boolean getIsChecked(final FacesContext context, final UIComponent component,
        final Object value, final Object[] submittedValues, final Object currentSelections, final Object itemValue,
        final Converter converter) {
        UISelectOne selectOne = (UISelectOne) component;
        Object newValue;
        Object curValue = selectOne.getSubmittedValue();

        Class type = String.class;
        if (curValue != null) {
            type = curValue.getClass();
            if (type.isArray()) {
                curValue = ((Object[]) curValue)[0];
                if (null != curValue) {
                    type = curValue.getClass();
                }
            } else if (Collection.class.isAssignableFrom(type)) {
                Iterator valueIter = ((Collection) curValue).iterator();
                if (null != valueIter && valueIter.hasNext()) {
                    curValue = valueIter.next();
                    if (null != curValue) {
                        type = curValue.getClass();
                    }
                }
            }
        }

        try {
            newValue = context.getApplication().getExpressionFactory().
                coerceToType(itemValue, type);
        } catch (ELException ele) {
            newValue = itemValue;
        } catch (IllegalArgumentException iae) {
            // If coerceToType fails, per the docs it should throw
            // an ELException, however, GF 9.0 and 9.0u1 will throw
            // an IllegalArgumentException instead (see GF issue 1527).
            newValue = itemValue;
        }
        return null != newValue && newValue.equals(curValue);
    }

    @Override
    protected boolean shouldReturn(final Object currentSelections, final Object itemValue) {
        return itemValue != null;
    }

    @Override
    protected Object getItemValue(final FacesContext context, final UIComponent component, final Object value,
        final Converter converter,
        final Object[] submittedValues) {
        UISelectOne selectOne = (UISelectOne) component;
        Object curValue = selectOne.getSubmittedValue();
        if (curValue == null) {
            curValue = selectOne.getValue();
        }

        Class type = String.class;
        if (curValue != null) {
            type = curValue.getClass();
            if (type.isArray()) {
                curValue = ((Object[]) curValue)[0];
                if (null != curValue) {
                    type = curValue.getClass();
                }
            } else if (Collection.class.isAssignableFrom(type)) {
                Iterator valueIter = ((Collection) curValue).iterator();
                if (null != valueIter && valueIter.hasNext()) {
                    curValue = valueIter.next();
                    if (null != curValue) {
                        type = curValue.getClass();
                    }
                }
            }
        }
        Object returnItemValue = value;

        return returnItemValue;
    }
}
