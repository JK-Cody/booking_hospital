<template>
<div class="app-container">

<!-- 列表查询框 --> <!-- element-ui格式 -->
<el-form :inline="true" class="demo-form-inline">  
   <el-form-item>
      <el-input v-model="searchObj.hoscode" placeholder="医院编号"/>
   </el-form-item>
   <el-form-item>
      <el-input v-model="searchObj.hosname" placeholder="医院名称"/>
   </el-form-item>
   <el-button type="primary" icon="el-icon-search" @click="getList()">查询</el-button>
   <el-button type="default" @click="resetData()">清空</el-button>
</el-form>

<!-- 列表项删除框 -->
<div>
   <el-button type="danger" size="mini" @click="removeRows()">批量删除</el-button>
</div>

<!-- 列表各项 --> 
<el-table     
  :data="list" stripe style="width: 100%" 
  @selection-change="handleSelectionChange">   <!-- 多选框 -->
<el-table-column type="selection" width="55"/>
<el-table-column type="index" width="50"/>
<el-table-column prop="hosname" label="医院名称"/>
<el-table-column prop="hoscode" label="医院编号"/>
<el-table-column prop="apiUrl" label="网址" width="250"/>
<el-table-column prop="contactsName" label="联系人姓名"/>
<el-table-column prop="contactsPhone" label="联系人手机"/>
<el-table-column label="状态" width="70">
  <template slot-scope="scope">
          {{ scope.row.status === 1 ? '可用' : '不可用' }}    
  </template>
</el-table-column>

<!-- 锁定开关 -->
<el-table-column prop="state" label="启用状态" width="65" align="center" show-overflow-tooltip>  
    <template slot-scope="scope">
        <el-switch
            v-model="scope.row.status"
            active-color="#5B7BFA"
            inactive-color="#dadde5"
            :active-value="1"    
            :inactive-value="0"
            @change="lockStatus(scope.row.id,scope.row.status)"     
        >
        </el-switch>
     </template>
</el-table-column>

<!-- 修改数据 -->
<el-table-column header-align="center" align="center"  label="修改" >
  <template slot-scope="scope"> 
   <router-link :to="'/hospitalSet/edit/'+scope.row.id">    
   <el-button type="primary" size="mini" icon="el-icon-edit"></el-button>
   </router-link>
  </template>
</el-table-column>

<!-- 删除数据 -->
<el-table-column label="删除" width="70" align="center">
   <template slot-scope="scope">
      <el-button type="danger" size="mini" 
         icon="el-icon-delete" @click="removeDataById(scope.row.id)"> </el-button>
   </template>
</el-table-column>

</el-table>


<!-- 分页 -->
<el-pagination
  :current-page="current"
  :page-size="limit"
  :total="total"
  style="padding: 30px 0; text-align: center;"
  layout="total, prev, pager, next, jumper"
  @current-change="getList"/>

</div>
</template>

<script>
import hospitalSet from'@/api/hospitalSet'

export default {
    data(){
     return{
         current:1, //当前页
         limit:3,   //每页数量
         searchObj:{}, //每页的对象
         list:[],  //对象集合
         total:0, //每页对象数
         multipleSelection: [],// 批量选择对象的集合
         }
    },
    created() {   //先于页面渲染之前执行的方法
        this.getList()
    },
    methods: {
       //列表项获取方法
        getList(page=1) {   //设置页面打开初始页
            this.current = page    //获取当前页数
        hospitalSet.findPageHospitalSet(this.current,this.limit,this.searchObj)  //调用controller方法
        .then(response => {
             this.list = response.data.data.records   //获取值的对象
             this.total = response.data.data.total  //获取值的数量
             console.log(response)
        })
        .catch(error => {
            console.log(error)
        })
        },

        //批量选择方法     
        handleSelectionChange(selection){
          this.multipleSelection = selection
        },

        //批量删除方法     
        removeRows(){
            this.$confirm('此操作将永久删除医院信息, 是否继续?', '提示', {         // 删除操作确认
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
            }).then(() => { //确定执行then方法
                var idList=[]
                    this.multipleSelection.forEach(element => {
                    idList.push(element.id)    //将循环的值保存
                });
            //调用接口方法
            hospitalSet.batchDeleteHospitalSet(idList)
            .then(response => {
                //提示框
                this.$message({
                type: 'success',
                message: '删除成功!'
                })
                //再次查询作为刷新页面
                this.getList(1)
            })
          }) 
        },

        //  删除方法       
        removeDataById(id){        
        this.$confirm('此操作将永久删除医院信息, 是否继续?', '提示', {         // 删除操作确认
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
            }).then(() => { //确定执行then方法
            //调用接口方法
            hospitalSet.deleteHospitalSet(id)
             .then(response => {
                //提示框
                this.$message({
                  type: 'success',
                  message: '删除成功!'
                })
                //再次查询作为刷新页面
                this.getList(1)
            })
          }) 
        },
        
        //锁定列表项和解锁方法
        lockStatus(id,status){
            hospitalSet.lockHospitalSet(id,status)
            .then(response =>{
                var page=this.current
                this.getList(page)
            })
        }
    }
}

</script>
