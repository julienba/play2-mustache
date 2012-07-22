# play2-mustache

Use [Mustache](http://mustache.github.com)  templating with [play2](http://www.playframework.org/)

##  How to install

* add dependencies in build file:

```
val appDependencies = Seq(
  "org.jba" %% "play2-mustache" % "0.4",
  "com.twitter" %% "util-core" % "4.0.1" // For Twitter handler 
)

val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
  resolvers += Resolver.url("julienba.github.com", url("http://julienba.github.com/repo/"))(Resolver.ivyStylePatterns),
  resolvers += Resolver.url("julienba.github.com", url("http://julienba.github.com/repo/"))(Resolver.ivyStylePatterns)
)
```

* add [play-mustache.js](https://github.com/julienba/play2-mustache/tree/master/project-code/public/javascript/play-mustache.js) your project asset

* add com.jba.Mustache in default template import or import it in your view files

```
val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
  //Import Mustache in all template
  //templatesImport += "org.jba.Mustache"
)
```

## How to use

Pur your mustache templates in directory `app/views/mustache/'

### In scala templating

```
@Mustache.render("YOUR_MUSTACHE_TEMPLATE.html",content)
```

### In javascript 

```
// In your main template
@Mustache.script

// In a javascript callback
PlayMustache.to_html('/YOUR_MUSTACHE_TEMPLATE.html', data);
```

## USE

* https://github.com/spullara/mustache.java/
* https://github.com/janl/mustache.js

# TODO

* include play-mustache.js properly 

