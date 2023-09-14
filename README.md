#### SwipeRecyclerView
支持侧滑菜单、长按拖拽、Header、Footer、Loading(加载更多)
#### 资源
|名字|资源|
|-|-|
|AAR|[swipe_recycler_view.aar](https://github.com/RelinRan/SwipeRecyclerView/blob/main/aar)|
|GitHub |[SwipeRecyclerView](https://github.com/RelinRan/SwipeRecyclerView)|
|Gitee|[SwipeRecyclerView](https://gitee.com/relin/SwipeRecyclerView)|
#### Maven
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
    implementation 'com.github.RelinRan:SwipeRecyclerView:2022.9.14.1'
}
```

#### Xml
```
<androidx.ui.widget.SwipeRecyclerView
  android:id="@+id/rv_content"
  android:layout_width="match_parent"
  android:layout_height="match_parent" />
```
#### Adapter
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
item布局 item_text
```
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/tv_item_text"
    android:layout_width="match_parent"
    android:layout_height="50dp"
    android:background="#6835C3"
    android:gravity="center_vertical"
    android:paddingHorizontal="10dp"
    android:text="item"
    android:textColor="@android:color/white" />
```
菜单布局 item_swipe_menu 
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
    android:orientation="horizontal">

    <TextView
        android:id="@+id/tv_delete"
        android:layout_width="80dp"
        android:layout_height="match_parent"
        android:background="#8743AE"
        android:gravity="center"
        android:text="Delete"
        android:textColor="@color/white" />

    <TextView
        android:id="@+id/tv_edit"
        android:layout_width="80dp"
        android:layout_height="match_parent"
        android:background="#5C8CC7"
        android:gravity="center"
        android:text="Edit"
        android:textColor="@color/white" />

</LinearLayout>
```
#### 设置加载
```
SwipeRecyclerView rv_content = findViewById(R.id.rv_content);
rv_content.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
//设置是否支持长按拖拽移动
rv_content.setLongPressDragEnabled(false);
//适配器
SwipeItemAdapter adapter = new SwipeItemAdapter(this);
//设置侧滑菜单可用
adapter.setShowSwipe(true);
//滑动到底部会自动显示加载更多
adapter.setShowLoading(true);
//设置加载更多颜色
adapter.setLoadingBackgroundColor(Color.parseColor("#6835C3"));
//设置加载更多监听
adapter.setOnLoadListener(() -> {
    //处理加载更多逻辑
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            adapter.setLoading(false);
        }
    },1000);
});
//设置此方法之前，在SwipeItemAdapter中holder.addItemClick(R.id.xxx);
adapter.setOnItemClickListener((apt, v, position) -> {
    if (v.getId()==R.id.tv_delete){
        apt.removeItem(position);
        //注意：删除item,一定需要调用此方法。
    }
    if (v.getId()==R.id.tv_edit){
        adapter.closeSwipe();
    }
});
rv_content.setAdapter(adapter);
//设置数据源
List<String> list = new ArrayList<>();
for (int i = 0; i < 50; i++) {
    list.add("Item - "+i);
}
adapter.setDataSource(list);
```
#### 长按拖拽
SwipeRecyclerView
```
SwipeRecyclerView rv_content = findViewById(R.id.rv_content);
rv_content.setLongPressDragEnabled(true);
```
RecyclerView
```
RecyclerView rv_content = findViewById(R.id.rv_content);
SwipeItemTouchHelperCallback callback = new SwipeItemTouchHelperCallback();
ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
touchHelper.attachToRecyclerView(rv_content);
```
#### Header功能
代码设置
```
SwipeItemAdapter adapter = new SwipeItemAdapter(this);
adapter.setHeaderArgs(xxxx);
View headerView = LayoutInflater.from(context).inflate(R.layout.xxx,null);;
adapter.setHeaderView(headerView);
```
xml设置
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
#### Footer功能
代码设置
```
SwipeItemAdapter adapter = new SwipeItemAdapter(this);
adapter.setFooterArgs(xxx);
View footerView = LayoutInflater.from(context).inflate(R.layout.xxx,null);;
adapter.setFooterView(footerView);
```
xml设置
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
#### Loading功能自定义
代码设置
```
SwipeItemAdapter adapter = new SwipeItemAdapter(this);
adapter.setLoadingArgs(xxxx);
View loadingView = LayoutInflater.from(context).inflate(R.layout.xxx,null);
loadingView.setId(R.id.item_loading_more);
adapter.setFooterView(loadingView);
```
xml设置
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
