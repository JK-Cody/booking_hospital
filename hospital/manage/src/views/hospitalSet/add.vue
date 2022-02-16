<template>
    <div class="app-container">
    添加和修改
      
      <el-form label-width="120px">
         <el-form-item label="医院名称">
            <el-input v-model="hospitalSet.hosname"/>
         </el-form-item>
         <el-form-item label="医院编号">
            <el-input v-model="hospitalSet.hoscode"/>
         </el-form-item>
         <el-form-item label="api基础路径">
            <el-input v-model="hospitalSet.apiUrl"/>
         </el-form-item>
         <el-form-item label="联系人姓名">
            <el-input v-model="hospitalSet.contactsName"/>
         </el-form-item>
         <el-form-item label="联系人手机">
            <el-input v-model="hospitalSet.contactsPhone"/>
         </el-form-item>
         <el-form-item>
            <el-button type="primary" @click="addOrUpdate">保存</el-button>
         </el-form-item>
      </el-form>

   </div>
</template>

<script>
import hospitalSet from'@/api/hospitalSet'

export default{
    data(){
        return{
            hospitalSet:{}   //变量
        }
    },
    created(){
        if(this.$route.params && this.$route.params.id){
                const id = this.$route.params.id
                this.getId(id)
        }
    },
    methods:{
        //增加列表项
        addItem(){
            hospitalSet.saveHospitalSet(this.hospitalSet)
        .then(response =>{
            this.$message({
            type: 'success',
            message: '添加成功!'
            })
            this.$router.push({path:'/hospitalSet/list'})  //跳转到list.vue页面
        })
    },
       //id查询
        getId(id){
          hospitalSet.getHospitalSet(id)
          .then(response =>{
                this.hospitalSet = response.data.data       
        })    
    },
    //修改列表项
    updateItem(){
            hospitalSet.updateHospitalSet(this.hospitalSet)
        .then(response =>{
            this.$message({
            type: 'success',
            message: '修改成功!'
            })
            this.$router.push({path:'/hospitalSet/list'})  //跳转到list.vue页面
        })
    },
    //判断属于增加还是修改,根据id是否已存在
    addOrUpdate(){
          if(!this.hospitalSet.id){
                this.addItem()
          }else{
                this.updateItem();
          }
    }
 } 
}
 </script>