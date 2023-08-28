<template>
  <section class="content">
    <div class="container-fluid">
      <div class="row">
        <div class="col-12">
          <div class="card">
            <!-- /.card-header -->
            <div class="card-body pt-5">
              <div class="row">
                <div class="col-4">
                  <table class="table-sum">
                    <tr class="p-1">
                      <td>業務数</td>
                      <td class="td-border-rd">{{ total }}</td>
                    </tr>
                  </table>
                </div>
                <div class="li-button">
                  <RpaPagination
                    :total-pages="totalPages"
                    :total="total"
                    :per-page="perPage"
                    :current-page="currentPage"
                    @pageChanged="onPageChange"
                    @pageSizeChanged="onPageSizeChange"
                  />
                </div>
                <div class="button-auto">
                  <button class="btn button-main mr-2" @click="resetSortFilter">フィルター解除</button>
                  <router-link
                    :to="{ name: 'business-management-form' }"
                    class="btn button-main"
                  >
                    <span class="text-white">業務台帳登録</span>
                  </router-link>
                </div>
                <div class="col-12">
                  <RpaTable
                    :columns="tableOptions.columns"
                    :fixed-column="tableOptions.fixedColumns"
                    :full-screen="false"
                  >
                    <template v-slot:table-header>
                      <tr>
                        <th v-for="column in tableOptions.columns" :key="column.fieldName" :class="column.class">
                          <span v-html="column.name"></span>
                          <filter-layout
                            v-model:filterBy="filterBy"
                            v-model:sortBy="sortBy"
                            :field-lable="column.fieldLable"
                            :field-name="column.fieldName"
                            :filter="column.filter"
                            :filter-option-api="filterOptionApi"
                            :sort="column.sort"
                            :clientFilterData="filterSource"
                            :active="filterFields[column.fieldName]?.length > 0"
                            :shouldReset="resetFilter"
                            :items="items"
                          />
                        </th>
                      </tr>
                    </template>
                    <template v-slot:table-body>
                      <tr
                        v-for="item in items"
                        :key="item.id"
                        :class="rowGray(`${item.jobStatusesName}`)">
                        <td>
                          <span
                            class="label label-primary link-job"
                            v-if="item?.number != null"
                            @click="gotoDetail(item?.id)">
                            {{ item?.number }}
                          </span>
                        </td>
                        <td>
                          <span class="label label-primary link-job" v-if="item?.rpaNumber != null">
                            <router-link :to="{ name: 'robot-detail', params: { id: item.ledgerId }, }">
                              {{ item?.rpaNumber }}
                            </router-link>
                          </span>
                          <span class="label label-primary link-job" v-else-if="item?.rpaNumber == null && item?.flgDpmt == true" >
                            <router-link :to="{ name: 'robot-form', query: { jobId: item.id } }">
                              新規登録
                            </router-link>
                          </span>
                          <span class="label label-primary link-job" style="background-color: #6ba6e2;" v-else-if="item?.rpaNumber == null && item?.flgDpmt == false" >
                            <router-link :to="{ name: '' }">
                              新規登録
                            </router-link>
                          </span>
                        </td>
                        <td class="text-left">{{ item?.departmentName }}</td>
                        <td class="text-left">{{ item?.category }}</td>
                        <td class="text-left">
                          {{ item?.name }}
                        </td>
                        <td class="text-left">{{ item?.personName}}</td>
                        <td class="text-left">{{ item?.jobRisk != null ? item?.jobRisk + '-' + item?.number : '' }}</td>
                        <td class="text-left">{{ item?.consigneeName }}</td>
                        <td class="text-left">{{ item?.jobStatusesName }}</td>
                        <td class="text-left">{{ item?.updatedAt }}</td>
                      </tr>
                    </template>
                  </RpaTable>
                </div>
              </div>
            </div>
            <!-- /.card-body -->
          </div>
        </div>
        <!-- /.col -->
      </div>
      <!-- /.row -->
    </div>
    <!-- /.container-fluid -->
  </section>
</template>

<script>
import RpaTable from "@/components/molecules/RpaTable";
import FilterLayout from "@/components/molecules/FilterLayout";
import BusinessMngtModal from "@/components/organisms/BusinessMngtModal";
import RequestUtil from "../utils/request-util";
import { RPAApi } from "../constants/api-url";
import FnCommon from "../utils/fn-common";
import { RPAMessage } from "../constants/message";
import RpaPagination from "@/components/molecules/RpaPagination";
import { toast } from '@/plugins/toast';

export default {
  $dataTable: null,
  name: "BusinessMngtView",
  components: { RpaTable, FilterLayout, BusinessMngtModal, RpaPagination },
  data() {
    return {
      showForm: false,
      show: true,
      filterOptionApi: RPAApi.BUSSINESS,
      resetFilter: false,
      filterBy: {
        key: null,
        values: null,
      },
      sortBy: {
        key: "id",
        order: "desc",
      },
      items: [],
      totalValue: {
        countJobId: null,
      },
      tableOptions: {
        columns: [
          /*Common*/
          { name: "業務番号", width: 120, fieldName: "number", filter: true, sort: true },
          {
            name: "ロボット番号",
            width: 120,
            fieldName: "rpaNumber",
            filter: true,
            sort: true,
          },
          {
            name: "部署名",
            width: 140,
            fieldName: "departmentName",
            filter: true,
            sort: true,
          },
          {
            name: "カテゴリー",
            width: 140,
            fieldName: "category",
            filter: true,
            sort: true,
          },
          { name: "業務名", width: 280, fieldName: "name", filter: true, sort: true },
          {
            name: "担当者名",
            width: 140,
            fieldName: "personName",
            filter: true,
            sort: true,
          },
          {
            name: "リスク管理番号",
            width: 140,
            fieldName: "jobRisk",
            filter: true,
            sort: true,
          },
          {
            name: "委託先",
            width: 140,
            fieldName: "consigneeName",
            filter: true,
            sort: true,
          },
          {
            name: "業務ステータス",
            width: 140,
            fieldName: "jobStatusesName",
            filter: true,
            sort: true,
          },
          {
            name: "最終更新日",
            width: 200,
            fieldName: "updatedAt",
            filter: true,
            sort: true,
          },
        ],
      },
      idUpdate: null,
      filterSource: [],
      filterFields: {
        number: null,
        rpaNumber: null,
        departmentName: null,
        category: null,
        name: null,
        personName: null,
        jobRisk: null,
        consigneeName: null,
        jobStatusesName: null,
        updatedAt: null,
      },
      currentPage: 1,
      totalPages: 0,
      total: 0,
      perPage: 100,
      queryString: null,
      backFromDetail: false,
      jobFilter: {}
    };
  },
  methods: {
    onPageChange(page) {
      this.currentPage = page;
      this.getList(false);
    },
    onPageSizeChange(pageSize) {
      this.currentPage = 1;
      this.perPage = pageSize;
      this.getList(false);
    },
    gotoDetail(jobId) {
      this.$router.push({name: 'business-management-detail', params: { id: jobId}});
    },
    openForm() {
      this.showForm = true;
    },
    onCreate() {
      this.idUpdate = null;
      this.showForm = true;
    },
    onUpdate(id) {
      this.idUpdate = id;
      this.showForm = true;
    },
    onDelete(id) {
      this.$confirm.msgBoxOk(RPAMessage.CONFIRM_DELETE).then((res) => {
        if (res) {
          RequestUtil.delete(RPAApi.DELETE_JOBS(id), RPAMessage.DELETE_SUCCESS).then(
            (res) => {
              if (res.status === 200) {
                this.getList();
              }
            }
          );
        }
      });
    },
    getList(resetFilterBox = true) {
      let filterValue = this.filterBy.values
        ? Object.values(this.filterBy.values)
            .map((v) => v)
            .join(",")
        : "";
      this.filterFields[this.filterBy.key] = filterValue;
      let query = '';
      if(this.backFromDetail == true && this.queryString != null) {
        query = this.queryString;
        this.backFromDetail = false;
      } else {
        query = `?page=${this.currentPage}&size=${this.perPage}&sortBy=${this.sortBy.key}&order=${this.sortBy.order}&filterFields=${encodeURIComponent(JSON.stringify(this.filterFields))}`;
      }
      this.jobFilter.queryString = query;
      this.jobFilter.filterFields = this.filterFields;
      this.jobFilter.filterBy = this.filterBy;
      this.jobFilter.sortBy = this.sortBy;
      this.jobFilter.currentPage = this.currentPage;
      this.jobFilter.perPage = this.perPage;
      sessionStorage.clear();
      sessionStorage.setItem("jobFilter",JSON.stringify(this.jobFilter));
      this.queryString = query;
      var departmentUser;
      var roleUser;
      var list;
      RequestUtil.get(RPAApi.GET_DETAIL_USER).then((res) => {
            if (res.status === 200) {
              res = res.data;
              departmentUser = res?.mdepartment?.name;
              roleUser = res?.mrole?.id;
            } else if(res.status === 404){
              return this.$router.push('login');
            }
          })
      
      RequestUtil.get(RPAApi.BUSSINESS + query).then((res) => {
        if (res.status === 200) {
           list = res.data.body.data;
          for (let i = 0; i < list.length; i++) {
            if(roleUser == 1) {
              Object.assign(list[i], {flgDpmt: true});
            } else {
              if(departmentUser == list[i]["departmentName"]) {
                Object.assign(list[i], {flgDpmt: true});
              } else {
                Object.assign(list[i], {flgDpmt: false});
              }
            }
          }
          this.items = list;
          this.total = res.data.body.metadata.totalElement;
          this.totalPages = res.data.body.metadata.totalPage;
        }
      });

      //get all data of filter to set to Filter box
      if(resetFilterBox || this.$route.params.backFromDetail == 'true') {
        this.$route.params.backFromDetail = false;
        // let page = 1;
        // let size = 2147483647;
        // let newQuery = query.replace(/(page=).*?(&)/,'$1' + page + '$2');
        // newQuery = newQuery.replace(/(size=).*?(&)/,'$1' + size + '$2');
        let newQuery = `?page=1&size=2147483647&sortBy=id&order=desc`;
        RequestUtil.get(RPAApi.BUSSINESS + newQuery).then((res) => {
          if (res.status === 200) {
            this.filterSource = res.data.body.data;
          }
        });
      }
    },
    formatTime(time) {
      return FnCommon.timeToHM(time);
    },
    rowGray(status) {
      if (status != "運用中") {
        return "inactive";
      }
    },
    sleep(ms) {
      return new Promise((resolve) => setTimeout(resolve, ms));
    },
    resetSortFilter(){
      this.resetFilter = !this.resetFilter;
      this.filterBy = {key: null, values: null};
      this.sortBy = {key: "id", order: "desc"};
      this.filterFields = {
        number: null,
        rpaNumber: null,
        departmentName: null,
        category: null,
        name: null,
        personName: null,
        consigneeName: null,
        jobStatusesName: null,
        updatedAt: null,
      }

      this.getList();
    },
  },
  mounted() {
    this.getList();
  },
  beforeMount() {
    if(this.$route.params.backFromDetail == 'true' && sessionStorage.jobFilter != undefined) {
      var data = JSON.parse(sessionStorage.jobFilter);
      this.queryString = data['queryString'];
      this.filterFields = data['filterFields'];
      this.filterBy = data['filterBy'];
      this.sortBy = data['sortBy'];
      this.currentPage = data['currentPage'];
      this.perPage = data['perPage'];
      this.backFromDetail = true;
    }
  },
  watch: {
    sortBy() {
        this.currentPage = 1;
        this.getList(false);
    },
    filterBy() {
      this.currentPage = 1;
      this.getList(false);
    }
  },
};
</script>

<style scoped>
.inactive {
  background-color: #bfbfbf !important;
}
</style>
