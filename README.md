[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/searchdropdown)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/searchdropdown.svg)](https://vaadin.com/directory/component/searchdropdown)

# SearchDropDown Add-on for Vaadin 8

[![Build Status](https://epic.siika.fi/jenkins/job/SearchDropDown%20(Vaadin)/badge/icon)](https://epic.siika.fi/jenkins/job/SearchDropDown%20(Vaadin)/)

SearchDropDown provides search field with drop down suggestions, designed to be used with asynchronous queries
required to find suitable suggestions.

## Online demo

Try the add-on demo at http://app.siika.fi/SearchDropDown

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews,
go to http://vaadin.com/addon/searchdropdown

## Building and running demo

git clone https://github.com/alump/SearchDropDown.git
mvn clean install
cd demo
mvn jetty:run

To see the demo, navigate to http://localhost:8080/

## Development with Eclipse IDE

For further development of this add-on, the following tool-chain is recommended:
- Eclipse IDE
- m2e wtp plug-in (install it from Eclipse Marketplace)
- Vaadin Eclipse plug-in (install it from Eclipse Marketplace)
- JRebel Eclipse plug-in (install it from Eclipse Marketplace)
- Chrome browser

### Importing project

Choose File > Import... > Existing Maven Projects

Note that Eclipse may give "Plugin execution not covered by lifecycle configuration" errors for pom.xml. Use "Permanently mark goal resources in pom.xml as ignored in Eclipse build" quick-fix to mark these errors as permanently ignored in your project. Do not worry, the project still works fine. 

### Debugging server-side

If you have not already compiled the widgetset, do it now by running vaadin:install Maven target for searchfield-root project.

If you have a JRebel license, it makes on the fly code changes faster. Just add JRebel nature to your searchfield-demo project by clicking project with right mouse button and choosing JRebel > Add JRebel Nature

To debug project and make code modifications on the fly in the server-side, right-click the searchfield-demo project and choose Debug As > Debug on Server. Navigate to http://localhost:8080/searchfield-demo/ to see the application.

### Debugging client-side

Debugging client side code in the searchfield-demo project:
  - run "mvn vaadin:run-codeserver" on a separate console while the application is running
  - activate Super Dev Mode in the debug window of the application or by adding ?superdevmode to the URL
  - You can access Java-sources and set breakpoints inside Chrome if you enable source maps from inspector settings.
 
## Release notes

### Version 0.3.0 (2018-02-12)
- Allow HTML content mode in dropdown items
- Easy way to highlight matching part of suggestions
- After search field blur do not popup suggestions
- Lot of SASS variables to allow you to adjust look'n feel
- Do not open pending dropdown if field has been blured
- Open dropdown after focus of field if value present

### Version 0.2.0 (2018-02-13)
- Increase Vaadin dependency to 8.3
- Fix dropdown alignment issues with Vaadin 8.3

### Version 0.1.1 (2017-04-26)
- Clearing field now emits a search event, identified by isClear method
- Click events are now fired if user clicks the icon in search field. Can be used as search initializer or something else.

### Version 0.1.0 (2017-04-17)
- Initial experimental release
- Currently expects to be used with Valo based themes

## Roadmap

This component is developed as a hobby with no public roadmap or any guarantees of upcoming releases. That said, the following features are planned for upcoming releases:
- Next step is to finalize the API, and stabilize initial feature set.

## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

SearchDropDown is written by Sami Viitanen sami.viitanen@vaadin.com

# Developer Guide

## Getting started

Here is a simple example on how to try out the add-on component (simple String mapped synchronous API usage).

```java
public static final String VALUES[] = { "alfa", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel",
    "india", "juliett", "kilo", "lima", "mike", "november", "oscar", "papa", "quebec", "romea", "sierra",
    "tango", "uniform", "victor", "whiskey", "xray", "yankee", "zulu" };

SimpleSearchDropDown simpleSearch = new SimpleSearchDropDown(query -> {
    // For empty query, do not provide any suggestions
    final String cleaned = query.toLowerCase().trim();
    if(cleaned.isEmpty()) {
        return Collections.EMPTY_LIST;
    }

    // Normally here you would perform database or REST query or queries, to find suitable suggestions.
    // Simple API is always synchronous, so when you want to go to asynchronous use base class.
    return Arrays.stream(VALUES).filter(v -> v.contains(cleaned)).collect(Collectors.toList());
});
simpleSearch.setPlaceHolder("Search Phonetic Alphabets");
simpleSearch.addSearchListener(e -> Notification.show("User selected: " + e.getText()));
```

<...>

For a more comprehensive example, see src/test/java/org/vaadin/alump/searchdropdown/demo/ExampleView.java

## API

SearchDropDown JavaDoc is available online at TODO
