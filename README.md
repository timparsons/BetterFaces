# BetterFaces
A better JSF component library than what comes with Mojarra

### Why BetterFaces?
Do you use JSF, but want to use a component library that is light weight and makes things better?  Me too.

BetterFaces is just that, light weight, and makes things better.  Rather than adding new widgets and the like, BetterFaces renders HTML in a better way (i.e. not using tables to organize a radio group) that is in line with HTML standards.

### Sounds pretty cool, how do I use it?
Simple!  BetterFaces is a Maven project, so all you need to do is include it as a dependency in your JSF web application*:

```xml
<dependency>
  <groupId>io.timparsons</groupId>
  <artifactId>betterfaces</artifactId>
  <version>0.0.1</version>
</dependency>
```

*until this project is pushed to http://mvnrepository.com/, you will need to include the jar in your project.  You can download the jar and reference to its location in your pom: 
```xml
<dependency>
  <groupId>io.timparsons</groupId>
  <artifactId>betterfaces</artifactId>
  <version>0.0.1</version>
  <scope>system</scope>
  <systemPath>${project.basedir}/src/main/resources/betterfaces-0.0.1.jar</systemPath>
</dependency>
```

### Alright, done.  Now what?
Now you can make your JSF better!  BetterFaces is written in a way that it will override the default Mojarra implementation of various renderers.  This means that you don't have to include another namespace in your JSF pages.

#### Better Tags Currently Implemented

##### h:selectOneRadio, h:selectManyCheckbox

No more rendering of radio groups and checkbox groups in a `table`.

Added attributes:

| Attribute | dataType | default | description |
| --- | --- | --- | --- |
| wrapLabel | boolean | `false` | controls whether the `input` tag should be wrapped in a `label` tag |
| labelWrapClass | string | `null` | If `wrapLabel` is `true`, then this class will be put on the `span` that will wrap the actual `label` text |
| wrapHtml | string | `null` | If `wrapLabel` is `true`, then the entire `input`/`label` will be wrapped in whatever HTML element is defined |
| htmlWrapClass | string | `null` | if `wrapHtml` is populated, then this class will be added to the HTML element specified in `wrapHtml` |

Default rendering without usage of BetterFaces attributes:
```html
<input type="radio|checkbox" name="inputName" value="inputValue" />
<label for="inputName">Label Value</label>
```

Setting `wrapLabel` to "true" and specifying `labelWrapClass` as "labelClass" results in:
```html
<label for="inputName">
  <input type="radio|checkbox" name="inputName" value="inputValue" />
  <span class="labelClass">Label Value</span>
</label>
```

With specifying `wrapHtml` to "div" and specifying `htmlWrapClass` as "fooBar" results in:
```html
<div class="fooBar">
  <label for="inputName">
    <input type="radio|checkbox" name="inputName" value="inputValue" />
    <span class="labelClass">Label Value</span>
  </label>
</div>
```

---

##### h:outputFormat
Adds support for named parameters in your parametarized string.  Still supports the numeric specification of parameters as originally implemented.

To use the named parameters, simply specify your parameter name inside the braces, and then for each `f:param` specify both the `value` and the `name`, where the `name` is a parameter specified in your string.  Note: order does not matter for the `f:param` within the `h:outputFormat`.

For example:

```html
<h:outputFormat value="Welcome, {one}. You have {two} new messages">
  <f:param name="two" value="3" />
  <f:param name="one" value="John" />
</h:outputFormat>
```

You can even mix named parameters and numeric parameters.  Note: order is important for the numbered parameters
```html
<h:outputFormat value="Welcome, {one}. You have {two} new messages. Your last login was at {0} from {1}">
  <f:param value="11:30pm" />
  <f:param value="New York" />
  <f:param name="two" value="3" />
  <f:param name="one" value="John" />
</h:outputFormat>
```
