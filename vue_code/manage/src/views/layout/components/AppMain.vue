<template>
  <section class="app-main">
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <!-- 添加key值保证路由切换时都会重新触发生命周期方法，确保组件被重新初始化 -->
        <router-view :key="key"/>  
      </keep-alive>
    </transition>
  </section>
</template>

<script>
export default {
  name: 'AppMain',
  computed: {
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.fullPath
    }
    // 避免组件重用问题的设置
    // key() {
    // returnthis.$route.name !== undefined? this.$route.name + +newDate(): this.$route + +newDate()
    // }
  }
}
</script>

<style scoped>
.app-main {
  /*84 = navbar + tags-view = 50 +34 */
  min-height: calc(100vh - 84px);
  width: 100%;
  position: relative;
  overflow: hidden;
}
</style>

