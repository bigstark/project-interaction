# project-interaction
[![license](https://img.shields.io/hexpm/l/plug.svg)](LICENSE)
[![jitpack](https://img.shields.io/badge/jitpack-1.1.0-green.svg)](https://jitpack.io/#bigstark/project-interaction)


I'm gonna develop various components and animations in this project.
I hope this project helps you use interaction.


## Include your project
add build.gradle
```
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```
```
dependencies {
    compile 'com.github.bigstark:project-interaction:1.1.1'
}
```

## Loading Indicator
When people use android application, loading is the most common things to happen. If screens do not show any changes while waiting for request or response, users think the application stops. Eventually, this occurs negative effects to users. That is why we need to tell users it's loading. Therefore Loading Indicator is a essential factor for application.


### Circle Loading View
![CircleLoadingView](https://github.com/bigstark/project-interaction/blob/master/screenshots/CircleLoadingView.gif)

- Usage
```xml
<com.bigstark.interaction.CircleLoadingView
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/circle_loading_view"
    android:layout_width="100dp"
    android:layout_height="100dp"
    app:circleColor="#FF0000"
    app:circleRadius="17dp"
    />
```


### Random Tile View
![RandomTileLoadingView](https://github.com/bigstark/project-interaction/blob/master/screenshots/RandomTileLoadingView.gif)

- Usage
```xml
<com.bigstark.interaction.RandomTileLoadingView
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/random_tile_loading_view"
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:layout_marginTop="20dp"
    app:tileColor="#00FF00"
    app:tileMargin="10dp"
    />
```

License
-------

    Copyright 2017 BigStarK

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
