# CircleMenu
An animated circle menu for Android
# Demo
![](https://raw.githubusercontent.com/qinwenbo114/CircleMenu/master/demo-images/demo.gif)
# Usage
* Add the following to your project level build.gradle:
```groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
* Add this to your app build.gradle:
```groovy
dependencies {
    compile 'com.github.qinwenbo114:CircleMenu:v1.0'
}
```
* Add this to layout xml file:
```xml
<com.qinwenbo.circlemenulib.CircleMenu
    xmlns:customView = "http://schemas.android.com/apk/res-auto"
    android:id="@+id/yourViewID"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    customView:startAngle="60"
    customView:openAngle="95"
    customView:menuIconRadius="30dp"
    customView:circlePathRadius="100dp"
    customView:isClockwise="true"
    customView:period="500"/>
```
    * Parameters specification
        * startAngle: Start angle of big arc sweep area. X direction is 0°, Y direction is 90°. Value range is [0,360)
        * openAngle: Sweep angel. Value range is [0,360).
        * menuIconRadius: Radius of every menu icon.
        * circlePathRadius: Radius of big arc.
        * isClockwise: Sweep direction.
        * period: Animation running time.
    * You can preview the sweep area in Android Studio like the following image
![](https://raw.githubusercontent.com/qinwenbo114/CircleMenu/master/demo-images/preview.png)

* Add this to Activity/Fragment file:
```java
CircleMenu circleMenu = (CircleMenu) findViewById(R.id.yourViewID);
List<MenuIcon> menuIcons = new ArrayList<>();
menuIcons.add(new MenuIcon(ContextCompat.getDrawable(this, R.drawable.yourMenuImage1)));
menuIcons.add(new MenuIcon(ContextCompat.getDrawable(this, R.drawable.yourMenuImage2)));
menuIcons.add(new MenuIcon(ContextCompat.getDrawable(this, R.drawable.yourMenuImage3)));
menuIcons.add(new MenuIcon(ContextCompat.getDrawable(this, R.drawable.yourMenuImage4)));
circleMenu.setMenuIcons(menuIcons);
circleMenu.setOnMenuSwitchListener(new CircleMenu.OnMenuSwitchListener() {
    @Override
    public void onMenuSwitch(int menuStatus, int currentMenuIndex) {
        Log.d("menuStatus", menuStatus+"");
        Log.d("menuIndex", currentMenuIndex+"");
    }
});
```

# License
Copyright 2017 Qinwenbo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
