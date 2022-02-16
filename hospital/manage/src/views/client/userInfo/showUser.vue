<template>
    <div class="app-container">
        <h4>用户信息</h4>
        <table class="table table-striped table-condenseda table-bordered" width="100%">
        <tbody>
            <tr>
                <th style="background-color: #f2f2f2"  width="15%">手机号</th>
                <td width="35%" ><b>{{ userInfo.phone }}</b></td>
                <th style="background-color: #f2f2f2" width="15%">用户姓名</th>
                <td width="35%">{{ userInfo.name }}</td>
            </tr>
        <tr>
            <th style="background-color: #f2f2f2" >状态</th>
            <td>{{ userInfo.status == 0 ? '锁定' : '正常' }}</td>
            <th style="background-color: #f2f2f2" >注册时间</th>
            <td>{{ userInfo.createTime }}</td>
        </tr>
        </tbody>
        </table>
        <h4>认证信息</h4>
        <table class="table table-striped table-condenseda table-bordered" width="100%">
            <tbody>
            <tr>
                <th style="background-color: #f2f2f2" width="15%">姓名</th>
                <td width="35%"><b>{{ userInfo.name }}</b></td>
                <th style="background-color: #f2f2f2" width="15%">证件类型</th>
                <td width="35%">{{ userInfo.certificatesType }}</td>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2">证件号</th>
                <td>{{ userInfo.certificatesNo }}</td>
                <th style="background-color: #f2f2f2" >证件图片</th>
                <td><img :src="userInfo.certificatesUrl" width="100px"></td>
            </tr>
            </tbody>
        </table>
    <h4>就诊人信息</h4>
    <el-table
        v-loading="listLoading"
        :data="patientList"
        stripe
            style="width: 100%">
        <el-table-column
        label="序号"
        width="120"
        height="120"
        align="center">
        <template slot-scope="scope">
                {{ scope.$index + 1 }}
        </template>
        </el-table-column>

        <el-table-column prop="name" label="姓名"/>
        <el-table-column prop="param.certificatesTypeString" label="证件类型"/>
        <el-table-column prop="certificatesNo" label="证件编号" width="170px"/>
        <el-table-column label="性别"  width="70px">
        <template slot-scope="scope" >
                {{ scope.row.sex == 1 ? '男' : '女' }}
        </template>
        </el-table-column>
        <el-table-column prop="birthdate" label="出生年月" width="110px"/>
        <el-table-column prop="phone" label="手机" width="110px"/>
        <el-table-column label="是否结婚">
        <template slot-scope="scope">
                {{ scope.row.isMarry == 1 ? '已婚' : '未婚' }}
        </template>
    </el-table-column>
    <!-- 使用prop="address" 无法显示 -->
    <el-table-column prop="param.fullAddress" label="地址"  width="200px"/>  
    <el-table-column prop="createTime" label="注册时间" width="150px"/>
    </el-table>
    <br>
    <el-row>
    <el-button  @click="back">返回</el-button>
    </el-row>
    </div>
</template>
<script>

import clientApi from '@/api/client'

export default {
    // 定义数据
    data() {
        return {
            listLoading: false, // 数据是否正在加载
            id: this.$route.params.id,
            userInfo: {}, // 会员信息
            patientList: [] // 就诊人列表
        }
    },
    // 当页面加载时获取数据
    created() {
        this.showUserDetail()
    },
    methods: {
        // 根据id查询会员记录
        showUserDetail() {
            clientApi.showUserDetail(this.id).then(response => {
                this.userInfo = response.data.data.userInfo
                this.patientList = response.data.data.patientList
            })
        },
        back() {
            window.history.back(-1)
        }
    }
}
</script>
