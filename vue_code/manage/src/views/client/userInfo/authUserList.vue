<template>
    <div class="app-container">
        <!--查询表单-->
        <el-form :inline="true" class="demo-form-inline">
            <el-form-item>
                <el-input v-model="searchObj.keyword" placeholder="姓名/手机"/>
            </el-form-item>

            <el-form-item label="创建时间">
                <el-date-picker
                    v-model="searchObj.createTimeBegin"
                    type="datetime"
                    placeholder="选择开始时间"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    default-time="00:00:00"
                    />
            </el-form-item>
            至
            <el-form-item>
                <el-date-picker
                    v-model="searchObj.createTimeEnd"
                    type="datetime"
                    placeholder="选择截止时间"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    default-time="00:00:00"
                    />
            </el-form-item>

            <el-button type="primary" icon="el-icon-search" @click="getUserList()">查询</el-button>
            <el-button type="default" @click="resetUserList()">清空</el-button>
        </el-form>

        <!-- 列表 -->
        <el-table
        v-loading="listLoading"
        :data="list"
        stripe
            style="width: 100%">

            <el-table-column
            label="序号"
            width="70"
            align="center">
                <template slot-scope="scope">
                        {{ (page - 1) * limit + scope.$index + 1 }}
                </template>
            </el-table-column>

            <el-table-column prop="name" label="姓名"/>
            <el-table-column prop="certificatesType" label="证件类型"/>
            <el-table-column prop="certificatesNo" label="证件号"/>
            <el-table-column prop="createTime" label="创建时间"/>

            <el-table-column label="操作" width="250" align="center">
                <template slot-scope="scope">
               <router-link :to="'/client/userInfo/showUser/'+scope.row.id">
                    <el-button type="primary" size="mini">查看</el-button>
                </router-link>

                <el-button type="primary" size="mini" @click="setUserAuthenticationStatus(scope.row.id, 2)">通过</el-button>
                <el-button type="danger" size="mini" @click="setUserAuthenticationStatus(scope.row.id, -1)">不通过</el-button>
                </template>
            </el-table-column>

        </el-table>

        <!-- 分页组件 -->
        <el-pagination
            :current-page="page"
            :total="total"
            :page-size="limit"
            :page-sizes="[5, 10, 20, 30, 40, 50, 100]"
            style="padding: 30px 0; text-align: center;"
            layout="sizes, prev, pager, next, jumper, ->, total, slot"
            @current-change="getUserList"
            @size-change="changeSize"
            />
    </div>
</template>
<script>
import clientApi from '@/api/client'

export default {

// 定义数据
data() {
    return {
        listLoading: true, // 数据是否正在加载
        list: null, // banner列表
        total: 0, // 数据库中的总记录数
        page: 1, // 默认页码
        limit: 10, // 每页记录数
        searchObj: {    
            authStatus: 1   // 查询表单对象状态为1的用户
        } 
    }
},

// 当页面加载时获取数据
created() {
    this.getUserList()
},

methods: {
    // 调用api层获取数据库中的数据
       getUserList(page = 1) {
        console.log('翻页。。。' + page)
        // 异步获取远程数据（ajax）
        this.page = page
            clientApi.getUserList(this.page, this.limit, this.searchObj).then(
                response => {
                this.list = response.data.data.records
                this.total = response.data.data.total
                // 数据加载并绑定成功
                this.listLoading = false
            }
        )
    },
    // 当页码发生改变的时候
    changeSize(size) {
        console.log(size)
        this.limit = size
        this.getUserList(1)
    },

    // 重置查询表单
    resetUserList() {
        console.log('重置查询表单')
        this.searchObj = {}
        this.getUserList()
    },
    // 审批
    setUserAuthenticationStatus(userId, authStatus) {
        // debugger
        this.$confirm('确定该操作吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => { // promise
                // 点击确定，远程调用ajax
            return clientApi.setUserAuthenticationStatus(userId, authStatus)
        }).then((response) => {
            this.getUserList(this.page)
            if (response.data.code) {
                this.$message({
                    type: 'success',
                    message: '操作成功!'
                })
            }
        })
    }
  }
}
</script>
