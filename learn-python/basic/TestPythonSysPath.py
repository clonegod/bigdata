"""
模块搜索路径

默认情况下，Python解释器会搜索当前目录、所有已安装的内置模块和第三方模块，搜索路径存放在sys模块的path变量中

当我们试图加载一个模块时，Python会在指定的路径下搜索对应的.py文件，如果找不到，就会报错
    ImportError: No module named mymodule

添加自己的搜索目录，有两种方法：
    1、直接修改sys.path，添加要搜索的目录：
        >>> import sys
        >>> sys.path.append('/Users/michael/my_py_scripts')
    2、设置环境变量PYTHONPATH，该环境变量的内容会被自动添加到模块搜索路径中。
"""

import sys

if __name__ == '__main__':
    l1 = sys.path
    for p in l1:
        print(p)
