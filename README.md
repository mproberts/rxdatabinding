# Databinding with RxJava for Android

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/mproberts/rxdatabinding.svg)](https://jitpack.io/#mproberts/rxdatabinding)

A suite of tools for connecting your plain old RxJava code to your UI using Android databinding.

## Download

Rx Databinding is available via [Jitpack](https://jitpack.io/#mproberts/rxdatabinding).

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

```
dependencies {
    compile 'com.github.mproberts:rxdatabinding:-SNAPSHOT'
}
```

## Demo

See [demo](demo) for a simple implementation of databinding using Dagger 2, RxJava 2, Rooms, and Glide.

## Usage

### Lists

```xml
<android.support.v7.widget.RecyclerView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:listitem="@layout/layout_contact_list_item"
    app:data="@{model.contactList}"
    app:itemLayout="@{@layout/layout_contact_list_item}"
    app:layoutManager="android.support.v7.widget.LinearLayoutManager" />
```

<table>
  <tr><td><code>app:data</code></td><td><code>FlowableList&lt;?&gt;</code></td><td>Binds a <code>FlowableList&lt;?&gt;</code> instance from the the model to the list as the data source. Any changes from the list will automatically be bound to the `model` parameter of the supplied item layout.</td></tr>
  <tr><td><code>app:itemLayout</code></td><td><code>@LayoutRes int</code></td><td>The layout file to use for each item in the list. Alternatively, you can supply an <code>app:itemLayoutCreator</code>.</td></tr>
  <tr><td><code>app:itemLayoutCreator</code></td><td><code>? extends ItemLayoutCreator</code></td><td>An item creator which will select an item layout to inflate based on the content of the model associated with the item.</td></tr>
  <tr><td><code>app:layoutManager</code></td><td><code>String</code></td><td>The layout manager to use to layout the items in the <code>RecyclerView</code></td></tr>
</table>

#### Using Item Layout Creators

For complex lists involving multiple item types, using item layout creators allows you to decide which layout to populate for an item given access to the model which will be displayed.

Three types of layout creators are bundled into the library, `BasicLayoutCreator`, `TypedLayoutCreator` and `PredicateLayoutCreator`. To use a layout creator, create a class which exposes a new instance of your layout creator via a factory method, import your factory class into your layout, and bind the `app:itemLayoutCreator` property.

```java
public class SimpleItemLayoutCreator {

    // BasicLayoutCreator
    public static RecyclerViewAdapter.ItemViewCreator createSimpleLayoutCreator() {
        return new BasicLayoutCreator(R.layout.layout_simple_item);
    }
    
    // TypedLayoutCreator
    public static RecyclerViewAdapter.ItemViewCreator createTypedLayoutCreator() {
        return new TypedLayoutCreator()
            .addLayout(R.layout.layout_user_item, UserViewModel.class)
            .addLayout(R.layout.layout_group_item, GroupViewModel.class);
    }
    
    // PredicateLayoutCreator
    public static RecyclerViewAdapter.ItemViewCreator createPredicateLayoutCreator() {
        return new PredicateLayoutCreator<User>() {
            @Override
            public int getLayoutResource(User model) {
                if (model.isPremium()) {
                    return R.layout.layout_premium_user_item;
                } else {
                    return R.layout.layout_user_item;
                }
            }
        };
    }
}
```

```xml
<layout>
  <data>
    <import type="com.demo.itemlayout.SimpleItemLayoutCreator" />
  </data>
  ...
  <android.support.v7.widget.RecyclerView
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      tools:listitem="@layout/layout_contact_list_item"
      app:data="@{model.contactList}"
      app:itemLayoutCreator="@{SimpleItemLayoutCreator.createSimpleLayoutCreator()}"
      app:layoutManager="android.support.v7.widget.LinearLayoutManager" />
</layout>
```

More complicated layout creators can be made by directly extending the `ItemViewCreator` class but given you should be keeping complexity out of your UI layer, simple predication should suffice.


## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.