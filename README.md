# play2-mustache

Use [Mustache](http://mustache.github.com)  templating with [play2](http://www.playframework.org/)

##  How to install

* add plugin dependencies in project/plugins.sbt

```
resolvers += Resolver.url("julienba.github.com", url("http://julienba.github.com/repo/"))(Resolver.ivyStylePatterns)
addSbtPlugin("org.jba" % "play2-plugins-mustache" % "1.1.3") // play 2.2
//addSbtPlugin("org.jba" % "play2-plugins-mustache" % "1.1.2") // play 2.1
// addSbtPlugin("org.jba" % "play2-plugins-mustache" % "1.0.4") // play 2.0
```

* add dependencies in build file:

```
val appDependencies = Seq(
  "org.jba" %% "play2-mustache" % "1.1.3" // play2.2.0
  //"org.jba" %% "play2-mustache" % "1.1.2" // play2.1
  // "org.jba" %% "play2-mustache" % "1.0.4" // play2.0
)

val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
  resolvers += Resolver.url("julienba.github.com", url("http://julienba.github.com/repo/"))(Resolver.ivyStylePatterns),

  // Mustache settings
  mustacheEntryPoints <<= (sourceDirectory in Compile)(base => base / "assets" / "mustache" ** "*.html"),

  mustacheOptions := Seq.empty[String],
  resourceGenerators in Compile <+= MustacheFileCompiler  
)
```

* add [mustache.js](https://github.com/janl/mustache.js/) in your project asset

* add org.jba.Mustache in default template import or import it in your view files

```
val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
  //Import Mustache in all template
  //templatesImport += "org.jba.Mustache"
)
```

## How to use

Put your mustache templates in directory `app/assets/mustache/`

### In scala templating

```
@Mustache.render("YOUR_MUSTACHE_TEMPLATE", content)
```

### In javascript 

```
// In your main template

<!-- File generate with all your templates in MUSTACHE_TEMPLATES array -->
<script src="@routes.Assets.at("javascripts/mustache-tmpl.js")" type="text/javascript" charset="utf-8"></script>

<!-- your version of mustache.js  not bundle in this project -->
<script src="@routes.Assets.at("javascripts/mustache-0.7.0.min.js")" type="text/javascript" charset="utf-8"></script>

// In javascript 
Mustache.render(MUSTACHE_TEMPLATES['YOUR_MUSTACHE_TEMPLATE'], content);
```

### Active the plugin

create a file conf/play.plugins with something like that inside:

```
1500:org.jba.MustachePlugin
```

