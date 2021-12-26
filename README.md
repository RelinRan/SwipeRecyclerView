# SwipeRecyclerView
支持侧滑菜单、长按拖拽、Header、Footer
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
	implementation 'com.github.RelinRan:SwipeRecyclerView:2021.12.26.1'
}
```
## 使用方法
### 1.xml布局
```
<com.androidx.widget.SwipeRecyclerView
  android:id="@+id/rv_content"
  android:layout_width="match_parent"
  android:layout_height="match_parent" />
```
### 2.页面代码
```
rv_content = findViewById(R.id.rv_content);
rv_content.setLayoutManager(new LinearLayoutManager(context));
//设置可以长按拖拽
rv_content.setLongPressDragEnabled(true);
//设置可以侧滑显示菜单
rv_content.setSwipeMenuEnable(true);
//继承RecyclerAdapter实现的SwipeAdapter
SwipeAdapter adapter = new SwipeAdapter(this);
adapter.setOnItemClickListener(new SwipeItemClick());
rv_content.setAdapter(adapter);

private class SwipeItemClick implements RecyclerAdapter.OnItemClickListener<String>{
    @Override
    public void onItemClick(RecyclerAdapter<String> adapter, View v, int position) {
       if (v.getId() == R.id.tv_delete) {
            adapter.removeItem(position);
            //操作完毕，必须调用关闭菜单栏
            rv_content.closeSwipeMenu();
       } else if (v.getId() == R.id.tv_edit) {
            //操作完毕，必须调用关闭菜单栏
            rv_content.closeSwipeMenu();
        }
     }
}
```
### 3.侧滑菜单 + itemView
注意：必须继承 RecyclerAdapter重写getItemSwipeMenuLayoutResId() 和 getItemLayoutResId();
```
    private class ItemAdapter extends RecyclerAdapter<String> {

        public ItemAdapter(Context context) {
            super(context);
        }

        @Override
        public int getHeaderLayoutResId() {
            //TODO:头部布局
            return R.layout.androidx_item_header;
        }

        @Override
        protected void onHeaderBindViewHolder(ViewHolder holder, int position) {
            super.onHeaderBindViewHolder(holder, position);
            // TODO:头部布局数据绑定
            holder.addItemClick(R.id.btn_header);
            holder.find(Button.class,R.id.btn_header).setText("Header - "+getItem(position));
        }

        @Override
        public int getFooterLayoutResId() {
            //TODO:脚部布局
            return  R.layout.androidx_item_footer;
        }

        @Override
        protected void onFooterBindViewHolder(ViewHolder holder, int position) {
            super.onFooterBindViewHolder(holder, position);
            //TODO:脚部布局数据绑定
            holder.find(Button.class,R.id.btn_footer).setText("Footer - "+getItem(position));
            holder.addItemClick(R.id.btn_footer);
        }

        @Override
        protected int getItemSwipeMenuLayoutResId() {
            //TODO:侧滑Item布局
            return R.layout.android_menu;
        }

        @Override
        protected void onSwipeBindViewHolder(ViewHolder holder, int position) {
            super.onSwipeBindViewHolder(holder, position);
            //TODO:侧滑Ite布局数据绑定
            holder.addItemClick(R.id.btn_item_name);
            holder.addItemClick(R.id.tv_delete);
            holder.addItemClick(R.id.tv_edit);
            holder.find(TextView.class, R.id.btn_item_name).setText(getItem(position));
        }

        @Override
        protected int getItemLayoutResId(int viewType) {
            //TODO:普通item布局
            return R.layout.androidx_items;
        }

        @Override
        protected void onItemBindViewHolder(ViewHolder holder, int position) {
            //TODO:普通item布局数据绑定
        }

    }

```
### 常用方法
1.获取触摸事件助手
```
 ItemTouchHelper getItemTouchHelper();
```
2.设置Item触摸助手回调
```
 setSwipeItemTouchHelperCallback(SwipeItemTouchHelperCallback callback);
```
3.设置是否自动处理移动逻辑
```
setDragMoveAuto(boolean dragMoveAuto);
```
4.设置是否自动处理选择逻辑
```
setSelectedAuto(boolean selectedAuto);
```
5.设置是否可长按拖拽
```
setLongPressDragEnabled(boolean longPressDragEnabled) ;
```
6.设置拖动标识
```
setDragFlags(int dragFlags);
```
7.设置滑动标识
```
setSwipeFlags(int swipeFlags);
```
8.设置触摸选中监听
```
setOnItemTouchSelectedChangedListener(OnItemTouchSelectedChangedListener listener);
```
9.设置长按拖拽移动监听
```
setOnItemTouchMoveListener(OnItemTouchMoveListener listener);
```
10.触摸横向滑动完成监听
```
setOnItemTouchSwipedListener(OnItemTouchSwipedListener  listener);
```
11.打开侧滑菜单
```
openSwipeMenu();
```
12.关闭侧滑菜单
```
closeSwipeMenu();
```
