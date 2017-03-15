# VUE项目开发完之后，需要将其打包部署到生产环境

# 安装httpd

# 打包项目程序，在项目根目录下运行
```
npm run build
```
build之后，会在项目根目录下产生一个dist目录，这个目录下会有一个`index.html`文件和一个名为`static`文件夹
# 拷贝打包后的文件到httpd服务器下
比如我安装的httpd在`/var/www/`下
于是，我需要将`static`目录放在此目录下，即把`static`文件夹放在`/var/www/`目录下，至于`index.html`文件可以放在这个目录下，也可以新建的一个文件夹下，最好新建一个文件夹，叫到项目的名字，然后把这个`index.html`文件放到新建的文件夹下，这样方便识别。
# 打开网页，查看效果
比如上面我在目录`/var/www/`下新建了一个`myproject`的目录，于是，我可以像以下那样查看网页
输入：`http://localhost/myproject/index.html`
okay， 正常情况下，你可以看到应有的效果了！^_^
