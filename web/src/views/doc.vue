<template>
    <a-layout>
        <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
            <a-row>
                <a-col :span="6">
                    <a-tree
                            v-if="level1.length > 0"
                            :tree-data="level1"
                            @select="onSelect"
                            :replaceFields="{title: 'name', key: 'id', value: 'id'}"
                            :defaultExpandAll="true"
                    >
                    </a-tree>
                </a-col>
                <a-col :span="18">
                    <!--是纯html展示，和wangEditor没什么关系-->
                    <div :innerHTML="html"></div>
                </a-col>
            </a-row>
        </a-layout-content>
    </a-layout>
</template>

<script lang="ts">
    import { defineComponent, onMounted, ref, createVNode } from 'vue';
    import axios from 'axios';
    import {message} from 'ant-design-vue';
    import {Tool} from "@/util/tool";
    import {useRoute} from "vue-router";

    export default defineComponent({
        name: 'Doc',
        setup() {
            const route = useRoute();
            const docs = ref();
            const html = ref();

            /**
             * 一级文档树，children属性就是二级文档
             * [{
             *   id: "",
             *   name: "",
             *   children: [{
             *     id: "",
             *     name: "",
             *   }]
             * }]
             */
            const level1 = ref(); // 一级文档树，children属性就是二级文档
            level1.value = [];

            /**
             * 数据查询
             **/
            const handleQuery = () => {
                axios.get("/doc/all/" + route.query.ebookId).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        docs.value = data.content;

                        level1.value = [];
                        level1.value = Tool.array2Tree(docs.value, 0);
                    } else {
                        message.error(data.message);
                    }
                });
            };

            /**
             * 文档content查询
             **/
            const handleQueryContent = (id: number) => {
                axios.get("/doc/get-content" , {params:{id: id}}).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        html.value = data.content;

                    } else {
                        message.error(data.message);
                    }
                });
            };

            const onSelect = (selectedKeys: any, info: any) => {
                console.log('selected', selectedKeys, info);
                //Ant Deisign Vue的树形控件可以有多个值，但项目中使用到的只能选择一个，故取数组第0个值
                if (Tool.isNotEmpty(selectedKeys)) {
                    // 加载内容
                    handleQueryContent(selectedKeys[0]);
                }
            };

            onMounted(() => {
                handleQuery();
            });

            return {
                level1,
                html,
                onSelect
            }
        }
    });
</script>