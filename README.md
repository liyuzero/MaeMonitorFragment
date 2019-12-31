## 使用方法
### 新增依赖
1. 在项目的根目录gradle新增仓库如下：

```java
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

2. 使用module依赖，新增依赖：

```java
    compile 'com.github.liyuzero:maeMonitorFragment:1.2.0'
```

### 具体调用（详情见demo）
1. 动态权限申请

```java
    MAEMonitorFragment.getInstance(MainActivity.this).requestPermission(new String[]{Manifest.permission.GET_ACCOUNTS},
        new MAEPermissionCallback() {
            @Override
            public void onPermissionApplySuccess() {
                Toast.makeText(MainActivity.this, "权限申请成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionApplyFailure(List<String> list, List<Boolean> list1) {
                Toast.makeText(MainActivity.this, "权限申请失败", Toast.LENGTH_SHORT).show();
            }
        });
```

2. 启动startActivityForResult，采取回调方式获取结果

```java
    MAEMonitorFragment.getInstance(MainActivity.this).startActivityForResult(new Intent(MainActivity.this,
                TestActivity.class).putExtra("isForResult", true), 0, new MAEActivityResultListener() {
            @Override
            public void onActivityResult(int i, int i1, Intent intent) {
                if(i == 0 && i1 == -1 && intent != null){
                    Toast.makeText(MainActivity.this, intent.getStringExtra("info"), Toast.LENGTH_SHORT).show();
                }
            }
        });
```

2. 监听Fragment或者Activity的生命周期

```java
    MAEMonitorFragment.getInstance(this).setLifecycleListener(new MAELifecycleListener() {
            @Override
            public void onDestroy() {
                super.onDestroy();
                Toast.makeText(getApplicationContext(), "TestActivity onDestroy...", Toast.LENGTH_SHORT).show();
            }
        });
```