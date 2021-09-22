<template>
    <a-layout-footer style="text-align: center">
        甲蛙电子书 <span v-show="user.id">欢迎：{{user.name}}</span>
    </a-layout-footer>
</template>

<script lang="ts">
    import { defineComponent, computed } from 'vue';
    import store from "@/store";

    export default defineComponent({
        name: 'the-footer',
        setup() {
            //获取vuex中定义的全局变量user
            const user = computed(() => store.state.user);

            return {
                user
            }
        }
    });
</script>