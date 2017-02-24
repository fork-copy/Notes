有三点，如下

# 1 创建共享bus
```
var bus = new Vue()
```

# 2 发送事件
```
// 触发组件 A 中的事件
bus.$emit('id-selected', 1)
```

# 3 监听事件
```
// 在组件 B 创建的钩子中监听事件
bus.$on('id-selected', function (id) {
  // ...
})
```
很多时候，以上三块代码并不是在同一个文件里，所以`bus`需要使用其他方法进行共享，我用的是以下方法：
```
/*
 * By extending the Vue prototype with a new '$bus' property
 * we can easily access our global event bus from any child component.
 */
Object.defineProperty(Vue.prototype, '$bus', {
  get() {
    return this.$root.bus;
  }
});

new Vue({
  el: '#app',
  router,
  data: {
    bus: bus // Here we bind our event bus to our $root Vue model.
  },
  render: h => h(App)
})
```
这个方法是将bus注册到`Vue`的原型中，然后在各个子模块中就可以使用`this.$bus`来访问这个共享`bus`。

例如，

监听事件`addressChanged`，
```
  export default {
    created() {
      this.$bus.$on('addressChanged', event => {
        console.log(event.msg);
        console.log(event.areaCode);
        // Do something
      });
    }
  }
```

注册事件`addressChanged`，并传递`msg`和`areaCode`。
```
this.$bus.$emit('addressChanged', {
          msg: 'Province select changed!',
          areaCode: that.provinceSelectedValue
        });
```

[1] https://cn.vuejs.org/v2/guide/components.html#非父子组件通信
[2] https://laracasts.com/discuss/channels/vue/use-a-global-event-bus
[3] https://forum.vuejs.org/t/create-event-bus-in-webpack-template/4546/2