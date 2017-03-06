# CircleMenu
An animated circle menu for Android
# Demo
![](https://raw.githubusercontent.com/qinwenbo114/CircleMenu/master/demo/demo.gif)
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
