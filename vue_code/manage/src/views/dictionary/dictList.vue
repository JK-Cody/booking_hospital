<template>
    <div class="app-container">

        <div class="el-toolbar" >
        <div class="el-toolbar-body" style="justify-content: flex-start;" >
        <!-- 输入文件按钮 --> 
        <el-button type="text"  style="font-size: 20px ; border: solid 2px green"  @click="importData"><i class="fa fa-plus"/> 导入文件</el-button>
         <!-- 输出文件按钮 -->
        <el-button style="font-size: 20px ; border: solid 2px red" type="text" @click="exportData" ><i class="fa fa-plus"/> 导出列表</el-button>
        </div>
        </div>

        <!-- 数据字典列表 -->
        <!-- lazy为延迟加载 -->
        <el-table  
        style="width: 100%" row-key="id" border lazy  
        :data="list"    
        :load="getChildrens" 
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}">    <!-- hash树实现递归 -->
            <el-table-column label="名称" width="230" align="left">
            <template slot-scope="scope">
            <span>{{ scope.row.name }}</span>
            </template>
            </el-table-column>

            <el-table-column label="编码" width="220">
            <template slot-scope="{row}">
                    {{ row.dictCode }}
            </template>
            </el-table-column>
            <el-table-column label="值" width="230" align="left">
            <template slot-scope="scope">
            <span>{{ scope.row.value }}</span>
            </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center">
            <template slot-scope="scope">
            <span>{{ scope.row.createTime }}</span>
            </template>
            </el-table-column>
        </el-table>

        <!-- 导入文件对话框 -->
        <el-dialog title="导入"  :visible.sync="dialogImportVisible" width="480px">
        <el-form label-position="right" label-width="170px">
        <el-form-item label="文件">
        <el-upload
        :multiple="false"    
        :on-success="importSuccess"
        :action="'http://localhost:8202/admin/cmn/dict/importData'"
        class="upload-demo">
        <el-button size="small" type="primary">点击上传</el-button>
        <div slot="tip" class="el-upload__tip">只能上传excel文件，且不超过500kb</div>
        </el-upload>
        </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
        <el-button @click="dialogImportVisible = false">
            取消
        </el-button>
        </div>
        </el-dialog>

    </div>
</template>

<script>
import dictionary from '@/api/dictionary'

export default {
    data() {
        return {
            list:[], //数据字典列表数组
            dialogImportVisible:false //对话框的出现判定
        }
    },
    created() {
        this.getDictList(1)  //1是父id
    },
    methods: {
       //将文件输入为列表
        importData(){
         this.dialogImportVisible = true  //将对话框设为打开
        },
        // 文件输入成功后续处理
        importSuccess(){
         this.dialogImportVisible = false 
         this.getDictList(1)
        },
        
       //将列表输出为文件
        exportData(){
           window.location.href = 'http://localhost:8202/admin/cmn/dict/exportData'
        },
         //数据字典列表——父id
        getDictList(id) {
            dictionary.dictionaryList(id)
                .then(response => {
                    this.list = response.data.data
                })
        },
        //数据字典列表——子id
        getChildrens(tree, treeNode, resolve) {
            dictionary.dictionaryList(tree.id).then(response => {
                resolve(response.data.data)
            })
        }
    }
}
</script>

