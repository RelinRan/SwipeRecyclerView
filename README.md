# SwipeRecyclerView
支持侧滑菜单、长按拖拽、Header、Footer、Loading(加载更多)
# 资源
|名字|资源|
|-|-|
|AAR|[swipe.aar](https://github.com/RelinRan/SwipeRecyclerView/blob/master/swipe_2022.7.25.1.aar)|
|GitHub |[SwipeRecyclerView](https://github.com/RelinRan/SwipeRecyclerView)|
|Gitee|[SwipeRecyclerView](https://gitee.com/relin/SwipeRecyclerView)|
## Maven
1.build.grade
```
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2./app/build.grade
```
dependencies {
    implementation 'com.github.RelinRan:SwipeRecyclerView:2022.7.25.1'
}
```

## 侧滑功能
### xml布局
```
<androidx.ui.widget.SwipeRecyclerView
  android:id="@+id/rv_content"
  android:layout_width="match_parent"
  android:layout_height="match_parent" />
```
### Adapter
注意：继承SwipeRecyclerAdapter ，<String>为item泛型
```
public class SwipeItemAdapter extends SwipeRecyclerAdapter<String> {

   public SwipeItemAdapter(Context context) {
       super(context);
   }

    @Override
    protected int getItemLayoutResId(int viewType) {
       // TODO: 普通Item布局 
       return R.layout.item_text;
    }

    @Override
    protected int getItemSwipeMenuLayoutResId() {
        // TODO: 侧滑菜单栏布局（例如：编辑、删除）
        // 注意xml高度:MATCH_PARENT，宽度：WRAP_CONTENT
        return R.layout.item_swipe_menu;
    }

    @Override
    protected void onSwipeBindViewHolder(ViewHolder holder, int position) {
       super.onSwipeBindViewHolder(holder, position);
       // TODO: 普通Item + 侧滑view数据绑定逻辑
       holder.addItemClick(R.id.tv_delete);
       holder.addItemClick(R.id.tv_edit);
       TextView textView = holder.find(R.id.tv_item_text);
       textView.setText(getItem(position));
    }

}
```
### Activity|Fragment
```
SwipeRecyclerView rv_content = findViewById(R.id.rv_content);
//设置侧滑菜单可用，此方法需要SwipeItemAdapter.setShowSwipe(true);
rv_content.setSwipeEnable(false);
//适配器
SwipeItemAdapter adapter = new SwipeItemAdapter(this);
//设置侧滑菜单可用
adapter.setShowSwipe(true);
//设置此方法之前，在SwipeItemAdapter中holder.addItemClick(R.id.xxx);
adapter.setOnItemClickListener((adapter, v, position) -> {
     switch (v.getId()){
         case R.id.tv_edit:

         break;
         case R.id.tv_delete:
            adapter.removeItem(position);
            //注意：删除item,一定需要调用此方法。
            rv_content.closeSwipe();
            break;
         }
});
```
## 长按拖拽
### RecyclerView
```
RecyclerView rv_content = findViewById(R.id.rv_content);
SwipeItemTouchHelperCallback callback = new SwipeItemTouchHelperCallback();
ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
touchHelper.attachToRecyclerView(rv_content);
```
### SwipeRecyclerView
```
SwipeRecyclerView rv_content = findViewById(R.id.rv_content);
rv_content.setLongPressDragEnabled(true);
```
## Header功能
Header功能支持SwipeRecyclerView、RecyclerView
### 代码设置
```
SwipeItemAdapter adapter = new SwipeItemAdapter(this);
adapter.setHeaderArgs(xxxx);
View headerView = LayoutInflater.from(context).inflate(R.layout.xxx,null);;
adapter.setHeaderView(headerView);
```
### Xml设置
```
public class SwipeItemAdapter extends SwipeRecyclerAdapter<String> {

    public SwipeItemAdapter(Context context) {
        super(context);
    }

    @Override
    public int getHeaderLayoutResId() {
        // TODO: Header布局
        return R.layout.xxx;
    }

    @Override
    protected void onHeaderBindViewHolder(ViewHolder holder, Bundle args) {
       super.onHeaderBindViewHolder(holder, args);
       // TODO: Header数据绑定逻辑,args数据来源于setHeaderArgs(xxx);
    }
}
```
## Footer功能
Footer功能支持SwipeRecyclerView、RecyclerView
### 代码设置
```
SwipeItemAdapter adapter = new SwipeItemAdapter(this);
adapter.setFooterArgs(xxx);
View footerView = LayoutInflater.from(context).inflate(R.layout.xxx,null);;
adapter.setFooterView(footerView);
```
### Xml设置
```
public class SwipeItemAdapter extends SwipeRecyclerAdapter<String> {

    public SwipeItemAdapter(Context context) {
        super(context);
    }

    @Override
    public int getFooterLayoutResId() {
        // TODO: Header布局
        return R.layout.xxx;
    }

    @Override
    protected void onFooterBindViewHolder(ViewHolder holder, Bundle args) {
       super.onFooterBindViewHolder(holder, args);
       // TODO: Footer数据绑定逻辑,args数据来源于setFooterArgs(xxx);
    }
}
```
## Loading功能(加载更多)
Loading功能支持SwipeRecyclerView、RecyclerView
### 代码设置
```
SwipeItemAdapter adapter = new SwipeItemAdapter(this);
adapter.setLoadingArgs(xxxx);
View loadingView = LayoutInflater.from(context).inflate(R.layout.xxx,null);;
adapter.setFooterView(loadingView);
```
### Xml设置
```
public class SwipeItemAdapter extends SwipeRecyclerAdapter<String> {

    public SwipeItemAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLoadingLayoutResId() {
        // TODO: Loading布局
        return R.layout.xxx;
    }

    @Override
    protected void onLoadingBindViewHolder(ViewHolder holder, Bundle args) {
       super.onLoadingBindViewHolder(holder, args);
       // TODO: Loading数据绑定逻辑,args数据来源于setLoadingArgs(xxx);
    }
}
```
## 获取触摸事件助手
```
 ItemTouchHelper getItemTouchHelper();
```
## 设置Item触摸助手回调
```
 setSwipeItemTouchHelperCallback(SwipeItemTouchHelperCallback callback);
```
## 设置是否自动处理移动逻辑
```
setDragMoveAuto(boolean dragMoveAuto);
```
## 设置是否自动处理选择逻辑
```
setSelectedAuto(boolean selectedAuto);
```
## 设置是否可长按拖拽
```
setLongPressDragEnabled(boolean longPressDragEnabled) ;
```
## 设置拖动标识
```
setDragFlags(int dragFlags);
```
## 设置滑动标识
```
setSwipeFlags(int swipeFlags);
```
## 设置触摸选中监听
```
setOnItemTouchSelectedChangedListener(OnItemTouchSelectedChangedListener listener);
```
## 设置长按拖拽移动监听
```
setOnItemTouchMoveListener(OnItemTouchMoveListener listener);
```
## 触摸横向滑动完成监听
```
setOnItemTouchSwipedListener(OnItemTouchSwipedListener  listener);
```
## 打开侧滑菜单
```
openSwipe();
```
## 关闭侧滑菜单
```
closeSwipe();
```
