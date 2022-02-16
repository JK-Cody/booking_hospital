<template>
  <div class="app-container">
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item>
        <el-input v-model="searchObj.keyword" placeholder="医院名称"/>
      </el-form-item>
      <el-form-item>
        <el-input v-model="searchObj.outTradeNo" placeholder="订单交易号"/>
      </el-form-item>
      <el-form-item>
        <el-input v-model="searchObj.patientName" placeholder="就诊人名称"/>
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="searchObj.createTimeBegin"
          type="date"
          placeholder="选择开始日期"
          value-format="yyyy-MM-dd" />
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="searchObj.createTimeEnd"
          type="date"
          placeholder="选择截止日期"
          value-format="yyyy-MM-dd" />
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="searchObj.reserveDate"
          type="date"
          placeholder="就诊日期"
          value-format="yyyy-MM-dd" />
      </el-form-item>
      <el-form-item>
        <el-select v-model="searchObj.orderStatus" placeholder="订单状态" class="v-select patient-select">
          <el-option
            v-for="item in statusList"
            :key="item.status"
            :label="item.comment"
            :value="item.status">
          </el-option>
        </el-select>
      </el-form-item>
      <el-button type="primary" icon="el-icon-search" @click="getOrderList()">查询</el-button>
      <el-button type="default" @click="resetOrderList()">清空</el-button>
    </el-form>
    <!-- 列表 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row>
      <el-table-column
        label="序号"
        width="60"
        align="center">
        <template slot-scope="scope">
          {{ (page - 1) * limit + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="outTradeNo" label="订单交易号" width="160"/>
      <el-table-column prop="hosname" label="医院名称" width="160"/>
      <el-table-column prop="depname" label="科室名称" width="160"/>
      <el-table-column prop="title" label="医生职称" />
      <el-table-column label="安排时间" width="130">
        <template slot-scope="scope">
          {{ scope.row.reserveDate }} {{ scope.row.reserveTime === 0 ? '上午' : '下午' }}
        </template>
      </el-table-column>
      <el-table-column prop="patientName" label="就诊人姓名" />
      <el-table-column prop="number" label="预约号序" width="80"/>
      <el-table-column prop="amount" label="服务费" width="70"/>
      <el-table-column prop="param.orderStatusString" label="订单状态" />
      <el-table-column prop="createTime" label="创建时间" width="156"/>
      <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
             <router-link :to="'/order/orderInfo/show/'+scope.row.id">
             <el-button type="primary" size="mini" icon="el-icon-edit">查看</el-button>
            </router-link>
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
      @current-change="getOrderList"
      @size-change="changeSize"
    />
  </div>
</template>

<script>

import orderApi from '@/api/order'

export default {
  data() {
    return {
      listLoading: true, // 数据是否正在加载
      list: null, 
      total: 0, // 数据库中的总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数
      searchObj: {}, // 查询表单对象
      statusList: []
    }
  },
  // 生命周期函数：内存准备完毕，页面尚未渲染
  created() {
    this.getOrderList()
    this.getOrderStatusList()
  },
  methods: {
    // 当页码发生改变的时候
    changeSize(size) {
      console.log(size)
      this.limit = size
      this.getOrderList(1)
    },
    //获取用户订单列表   
    getOrderList(page = 1) {   //设置页面打开初始页
     console.log('翻页。。。' + page)
      this.page = page  //获取当前页数
        orderApi.getOrderList(this.page, this.limit, this.searchObj)
        .then(response => {
          
            this.list = response.data.data.records
            this.total = response.data.data.total
            // 数据加载并绑定成功
            this.listLoading = false
          }
      )
    },
    //获取用户订单状态列表
    getOrderStatusList() {
      orderApi.getOrderStatusList().then(response => {
        this.statusList = response.data.data
      })
    },
    // 重置查询表单
    resetOrderList() {
      console.log('重置查询表单')
      this.searchObj = {}
      this.getOrderList()
    }
  }
}
</script>
