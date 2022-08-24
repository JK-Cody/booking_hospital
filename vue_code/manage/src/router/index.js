import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/views/layout/Layout'

/* Router Modules */
import componentsRouter from './modules/components'
import chartsRouter from './modules/charts'
import tableRouter from './modules/table'
import nestedRouter from './modules/nested'

/** note: Submenu only appear when children.length>=1
 *  detail see  https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 **/

/**
* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
* alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
*                                if not set alwaysShow, only more than one route under the children
*                                it will becomes nested mode, otherwise not show the root menu
* redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
* name:'router-name'             the name is used by <keep-alive> (must set!!!)
* meta : {
    roles: ['admin','editor']     will control the page roles (you can set multiple roles)
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
    noCache: true                if true ,the page will no be cached(default is false)
  }
**/
export const constantRouterMap = [
  
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/authredirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/errorPage/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/errorPage/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: 'dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: 'dashboard', icon: 'dashboard', noCache: true }
      }
    ]
  },
  {
    path: '/documentation',
    component: Layout,
    redirect: '/documentation/index',
    children: [
      {
        path: 'index',
        component: () => import('@/views/documentation/index'),
        name: 'Documentation',
        meta: { title: 'documentation', icon: 'documentation', noCache: true }
      }
    ]
  },
]


export default new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})


export const asyncRouterMap = [
    {
      path: '/permission',
      component: Layout,
      redirect: '/permission/index',
      alwaysShow: true, // will always show the root menu
      meta: {
        title: 'permission',
        icon: 'lock',
        roles: ['admin', 'editor'] // you can set roles in root nav
      },
    children: [
      {
        path: 'page',
        component: () => import('@/views/permission/page'),
        name: 'PagePermission',
        meta: {
          title: 'pagePermission',
          roles: ['admin'] // or you can only set roles in sub nav
        }
      },
    {
      path: 'directive',
      component: () => import('@/views/permission/directive'),
      name: 'DirectivePermission',
      meta: {
        title: 'directivePermission'
        // if do not set roles, means: this page does not require permission
      }
      }
    ]
  },

  {
    path: '/icon',
    component: Layout,
    children: [
      {
        path: 'index',
        component: () => import('@/views/svg-icons/index'),
        name: 'Icons',
        meta: { title: 'icons', icon: 'icon', noCache: true }
      }
    ]
  },

  /** When your routing table is too long, you can split it into small modules**/
  componentsRouter,
  chartsRouter,
  nestedRouter,
  tableRouter,

// 医院列表路由
  {
    path: '/hospitalSet',  
    component: Layout,
    redirect: '/hospitalSet/list',
    name: '医院设置管理',
    meta: {
      title: '医院设置管理',
      icon: 'example'
    },
    children: [
      // list.vue
      {
        path: 'list',
        component: () => import('@/views/hospitalSet/list'),
        name: '列表',
        meta: { title: '成员列表', icon: 'list' }
      },
      // add.vue
      {
        path: 'add',    
        component: () => import('@/views/hospitalSet/add'),
        name: '添加',
        meta: { title: '添加成员', icon: 'add'},
      },
      { 
        path: 'edit/:id',      //引用到hospitalSet的router-link
        component: () => import('@/views/hospitalSet/add'),
        name: '修改',
        meta: { title: '修改成员', noCache: true},
        hidden:true
      }, 
    ]
  },
  //hospitalList列表路由
  {
    path: '/hospitalList',
    component: Layout,
    redirect: '/hospitalList/list',
    name: '医院列表管理',
    meta: { title: '医院列表管理', icon: 'hospitalList' },
    children: [
      {
        path: 'list',     //被list.vue的router-link连接
        component: () => import('@/views/hospitalList/list'),
        name: '等级列表',
        meta: { title: '等级列表', icon: 'list' }
      },   
      {
        path: 'showHospital/:id',    
        name: '查看',
        component: () => import('@/views/hospitalList/showHospital'),
        meta: { title: '查看', noCache: true },
        hidden: true
      },
      {
        path: 'schedule/:hoscode',
        name: '排班',
        component: () => import('@/views/hospitalList/schedule'),
        meta: { title: '排班', noCache: true },
        hidden: true
      }      
    ]
  },
// dictionary列表路由
  {
    path: '/dictionary',
    component: Layout,
    redirect: '/dictionary/dictList',
    name: '数据管理',
    alwaysShow: true,  //显示子目录
    meta: { title: '数据管理', icon: 'dictList' },
    children: [
      {
        path: 'dictList',
        name: '数据字典',
        component: () => import('@/views/dictionary/dictlist'),
        meta: { title: '数据字典', icon: 'dictListChildren' }
      }
    ]
  },
// 用户管理
  {
    path: '/client',
    component: Layout,
    redirect: '/client/userInfo/list',
    name: 'userInfo',
    meta: { title: '用户管理', icon: 'table' },
    alwaysShow: true,
    children: [
      {
        path: 'userInfo/list',
        name: '用户列表',
        component: () =>import('@/views/client/userInfo/list'),
        meta: { title: '用户列表', icon: 'table' }
      },
      {
        path: 'userInfo/showUser/:id',
        name: '查看用户详细',
              component: () =>import('@/views/client/userInfo/showUser'),
        meta: { title: '查看用户详细' },
        hidden: true
      },
      {
        path: 'userInfo/authUserList',
        name: '认证审批的操作列表',
              component: () =>import('@/views/client/userInfo/authUserList'),
        meta: { title: '认证审批列表', icon: 'table' }
      }      
    ]
  },
//订单管理
{
  path: '/order',
  component: Layout,
  redirect: '/order/orderInfo/list',
  meta: { title: '订单管理', icon: 'table' },
  alwaysShow: true,
  children: [
      {
        path: 'orderInfo/list',
        name: '订单列表',
        component: () =>import('@/views/order/orderInfo/list'),
        meta: { title: '订单列表' }
      },
      {
        path: 'orderInfo/show/:id',
        name: '查看',
        component: () =>import('@/views/order/orderInfo/show'),
        meta: { title: '查看', noCache: true },
        hidden: true
      }
    ]
},
//统计管理
{
  path: '/statistics',
  component: Layout,
  redirect: '/statistics/orderChart/index',
  meta: { title: '统计管理', icon: 'table' },
  alwaysShow: true,
  children: [
      {
  path: 'orderChart/index',
  name: '预约统计',
  component: () =>import('@/views/statistics/orderChart/index'),
  meta: { title: '预约统计' }
      }
    ]
},


// 网页错误路由
{
  path: '/error',
  component: Layout,
  redirect: 'noredirect',
  name: 'ErrorPages',
  meta: {
    title: 'errorPages',
    icon: '404'
  },
  children: [
    {
      path: '401',
      component: () => import('@/views/errorPage/401'),
      name: 'Page401',
      meta: { title: 'page401', noCache: true }
    },
    {
      path: '404',
      component: () => import('@/views/errorPage/404'),
      name: 'Page404',
      meta: { title: 'page404', noCache: true }
    }
  ]
},

  {
    path: '/error-log',
    component: Layout,
    redirect: 'noredirect',
    children: [
      {
        path: 'log',
        component: () => import('@/views/errorLog/index'),
        name: 'ErrorLog',
        meta: { title: 'errorLog', icon: 'bug' }
      }
    ]
  },

  {
    path: '/excel',
    component: Layout,
    redirect: '/excel/export-excel',
    name: 'Excel',
    meta: {
      title: 'excel',
      icon: 'excel'
    },
    children: [
      {
        path: 'export-excel',
        component: () => import('@/views/excel/exportExcel'),
        name: 'ExportExcel',
        meta: { title: 'exportExcel' }
      },
      {
        path: 'export-selected-excel',
        component: () => import('@/views/excel/selectExcel'),
        name: 'SelectExcel',
        meta: { title: 'selectExcel' }
      },
      {
        path: 'upload-excel',
        component: () => import('@/views/excel/uploadExcel'),
        name: 'UploadExcel',
        meta: { title: 'uploadExcel' }
      }
    ]
  },

  {
    path: '/zip',
    component: Layout,
    redirect: '/zip/download',
    alwaysShow: true,
    meta: { title: 'zip', icon: 'zip' },
    children: [
      {
        path: 'download',
        component: () => import('@/views/zip/index'),
        name: 'ExportZip',
        meta: { title: 'exportZip' }
      }
    ]
  },

  {
    path: '/theme',
    component: Layout,
    redirect: 'noredirect',
    children: [
      {
        path: 'index',
        component: () => import('@/views/theme/index'),
        name: 'Theme',
        meta: { title: 'theme', icon: 'theme' }
      }
    ]
  },

  {
    path: '/clipboard',
    component: Layout,
    redirect: 'noredirect',
    children: [
      {
        path: 'index',
        component: () => import('@/views/clipboard/index'),
        name: 'ClipboardDemo',
        meta: { title: 'clipboardDemo', icon: 'clipboard' }
      }
    ]
  },

  {
    path: '/i18n',
    component: Layout,
    children: [
      {
        path: 'index',
        component: () => import('@/views/i18n-demo/index'),
        name: 'I18n',
        meta: { title: 'i18n', icon: 'international' }
      }
    ]
  },

  {
    path: 'external-link',
    component: Layout,
    children: [
      {
        path: 'https://github.com/PanJiaChen/vue-element-admin',
        meta: { title: 'externalLink', icon: 'link' }
      }
    ]
  },
    
  { path: '*', redirect: '/404', hidden: true }
]
