<template>
  <modal :show="showModal" @hide="hideModal">
      <label>フォルダパスをコピーしてファイルサーバーのファイルを参照して下さい</label>
    <template v-slot:footer>
      <button class="btn button-main mr-2" @click="copy(currentFilePath)">コピー</button>
      <button class="btn button-main" @click="hideModal">キャンセル</button>
    </template>
  </modal>
  <section class="content">
    <div class="container-fluid">
      <div class="row">
        <div class="col-12">
          <div class="card">
              <div class="col-12 mb-0 text-right top-button">
                <button class="btn button-main mr-2" @click="backToList">戻る</button>
                <button class="btn button-main" @click="onSubmit" id="btnSubmit" style="display: none;">編集</button>
              </div>
            <div class="card-header text-left">
              <div class="screen-name">業務台帳</div>
            </div>
            <!--業務台帳-->
            <!-- /.card-header -->
            <div class="card-body py-md-1">
              <div class="row content-data">
<!--                <div class="col-12 mb-1 text-right">-->
<!--                  <button class="btn button-main mr-2" @click="backToList">戻る</button>-->
<!--                  <button class="btn button-main" @click="onSubmit">編集</button>-->
<!--                </div>-->
                <div class="col-12 mb-3">
                  <table class="rpa-table-form padding_td_form_table">
                    <!--jobs.number-->
                    <tr>
                      <td>業務番号</td>
                      <td class="text-left">
                        {{ formData.number }}
                      </td>
                    </tr>
                    <!--jobs.name-->
					          <tr>
                      <td>業務名</td>
                      <td class="text-left">
                        {{ formData.name }}
                      </td>
                    </tr>
                    <tr>
                      <td>業務ステータス</td>
                      <td class="text-left">
                        {{ formData.status }}
                      </td>
                    </tr>
                  </table>
                </div>
                <div class="col-12 mb-3 form-scroll">
                  <table class="rpa-table-form width_td_form_table padding_td_form_table">
                    <tr>
                      <td>マニュアル</td>
                      <td>
                        <div class="input-group">
                          <a href="#" @click="onDownload(formData.fileName, 1)">
                            {{ formData.fileName }}
                          </a>
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <td>マニュアルファイルパス </td>
                      <td v-if="this.formData.jobRelated.manualFilePath" class="manual_file_path" v-on:click="gotoFormDetailModal(formData.jobRelated.manualFilePath)">
                        {{ formData.jobRelated.manualFilePath }}
                      </td>
                    </tr>
                    <tr>
                      <td>カルテ</td>
                      <td>
                        <div class="input-group">
                          <a href="#" @click="onDownload(formData.fileName2, 2)">
                            {{ formData.fileName2 }}
                          </a>
                          <!--                            <a href="C:\Source\Tact System\Source\webapp\source\upload\rpa_spec\1643265279_TTM_DB_RPAjp_20220114_R2.xlsx" download>dsdsds{{ formData.jobRelated.attachment1 }}</a>-->
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <td>部署番号</td>
                      <td class="text-left">
                        {{ formData.departmentNumber }}
                      </td>
                    </tr>
                    <!--jobs.department-->
                    <tr>
                      <td>部署名</td>
                      <td class="text-left">
                        {{ formData.department }}
                      </td>
                    </tr>
                    <!--jobs.ledger_id-->
                    <tr>
                      <td>ロボット番号</td>
                      <td class="text-left">
                        {{ formData.rpaNumber }}
                      </td>
                    </tr>
                    <!--jobs.category-->
                    <tr>
                      <td>カテゴリー</td>
                      <td class="text-left">
                        {{ formData.category }}
                      </td>
                    </tr>
                    <tr>
                      <td>業務概要・動作</td>
                      <td class="text-left">
                        <span style="white-space: pre-line">{{ formData.description }}</span>
                      </td>
                    </tr>
                    <tr>
                      <td>担当者社員番号</td>
                      <td class="text-left">
                        {{ formData.personNumber }}
                      </td>
                    </tr>
                    <tr>
                      <td>担当者氏名</td>
                      <td class="text-left">
                        {{ formData.jobRpaInfo.personName }}
                      </td>
                    </tr>
                    <tr>
                      <td>委託フラグ</td>
                      <td class="text-left">
                        {{ formData.jobRpaInfo.consignFlag }}
                      </td>
                    </tr>
                    <tr>
                      <td>委託先</td>
                      <td class="text-left">
                        {{ formData.jobRpaInfo.consignee }}
                      </td>
                    </tr>
                    <tr>
                      <td>[A]1日あたりの作業時間（h）</td>
                      <td class="text-left">
                        {{ formData.jobAnalyzeTime.workingTime }}
                        <span v-if="this.formData.jobAnalyzeTime.workingTime">h</span>
                      </td>
                    </tr>
                    <tr>
                      <td>[B]1カ月あたりの作業日数（●日）</td>
                      <td class="text-left">
                        {{ formData.jobAnalyzeTime.workingDays }}
                        <span v-if="this.formData.jobAnalyzeTime.workingDays">日</span>
                      </td>
                    </tr>
                    <tr>
                      <td>[C]1年あたりの作業月数（●カ月）</td>
                      <td class="text-left">
                        {{ formData.jobAnalyzeTime.workingMonths }}
                        <span v-if="this.formData.jobAnalyzeTime.workingMonths">カ月</span>
                      </td>
                    </tr>
                    <tr>
                      <td>年間稼働時間（ｈ）[A]ｘ[B]ｘ[C]</td>
                      <td class="text-left">
                        {{ formData.jobAnalyzeTime.yearlyTime }}
                        <span v-if="this.formData.jobAnalyzeTime.yearlyTime">ｈ</span>
                      </td>
                    </tr>
                    <tr>
                      <td>改善前年間工数</td>
                      <td class="text-left">
                        {{ formData.jobAnalyzeTime.beforeKaizenTime }}
                        <span v-if="this.formData.jobAnalyzeTime.beforeKaizenTime">ｈ</span>
                      </td>
                    </tr>
                    <tr>
                      <td>削減工数</td>
                      <td class="text-left">
                        {{ formData.jobAnalyzeTime.sakugenKosu }}
                        <span v-if="this.formData.jobAnalyzeTime.sakugenKosu">h</span>
                      </td>
                    </tr>
                    <tr>
                      <td>最終改善日</td>
                      <td class="text-left">
                        {{ formData.jobAnalyzeTime.lastKaizenDate }}
                      </td>
                    </tr>
                    <tr>
                      <td>頻度単位</td>
                      <td class="text-left">
                        {{ formData.jobAnalyzeTime.jobInterval }}
                      </td>
                    </tr>
                    <tr>
                      <td>難易度</td>
                      <td class="text-left">
                        {{ formData.difficultyName }}
                      </td>
                    </tr>
                    <tr>
                      <td>マニュアル</td>
                      <td class="text-left">
                        {{ formData.manualName }}
                      </td>
                    </tr>
                    <tr>
                      <td>定形/非定形</td>
                      <td class="text-left">
                        {{ formData.jobRelated.format }}
                      </td>
                    </tr>
                    <tr>
                      <td>属人度合</td>
                      <td class="text-left">
                        {{ formData.jobRelated.personalized }}
                      </td>
                    </tr>
                    <tr>
                      <td class="job-risk">リスクレベル</td>
                      <td class="text-left">
                        {{ formData.jobRisk.riskLevelJob }}
                      </td>
                    </tr>
                    <tr>
                      <td class="job-risk">精算分類</td>
                      <td class="text-left">
                        {{ formData.jobRisk.paymentMethodName }}
                      </td>
                    </tr>
                    <tr>
                      <td class="job-risk">精算根拠書類1</td>
                      <td class="text-left">
                        <a href="#" @click="onDownload(formData.fileName3, 3)">
                            {{ formData.fileName3 }}
                        </a>
                      </td>
                    </tr>
                    <tr>
                      <td class="job-risk">精算根拠書類ファイルパス1</td>
                      <td v-if="this.formData.jobRisk.manualFilePath1" class="manual_file_path" v-on:click="gotoFormDetailModal(formData.jobRisk.manualFilePath1)">
                        {{ formData.jobRisk.manualFilePath1 }}
                      </td>
                    </tr>
                    <tr>
                      <td class="job-risk">精算根拠書類2</td>
                      <td class="text-left">
                        <a href="#" @click="onDownload(formData.fileName4, 4)">
                            {{ formData.fileName4 }}
                        </a>
                      </td>
                    </tr>
                    <tr>
                      <td class="job-risk">精算根拠書類ファイルパス2</td>
                      <td v-if="this.formData.jobRisk.manualFilePath2" class="manual_file_path" v-on:click="gotoFormDetailModal(formData.jobRisk.manualFilePath2)">
                        {{ formData.jobRisk.manualFilePath2 }}
                      </td>
                    </tr>
                    <tr>
                      <td class="job-risk">金額</td>
                      <td class="text-left">
                        {{ formData.jobRisk.amountCategoryName }}
                      </td>
                    </tr>
                    <tr>
                      <td class="job-risk">データ作成方法</td>
                      <td class="text-left">
                        {{ formData.jobRisk.dataCreationName }}
                      </td>
                    </tr>
                    <tr>
                      <td>提供元（データ提供、前作業）</td>
                      <td class="text-left">
                        {{ formData.jobRelated.provideFrom }}
                      </td>
                    </tr>
                    <tr>
                      <td>提供先（電話・メール対応）</td>
                      <td class="text-left">
                        {{ formData.jobRelated.provideTo }}
                      </td>
                    </tr>
                    <tr>
                      <td>提供方法</td>
                      <td class="text-left">
                        {{ formData.jobRelated.provideMethod }}
                      </td>
                    </tr>
                    <tr>
                      <td>提供先詳細</td>
                      <td class="text-left">
                        {{ formData.jobRelated.provideToDetail }}
                      </td>
                    </tr>
                    <tr>
                      <td>責任者社員番号（社内主担当者）</td>
                      <td class="text-left">
                        {{ formData.mainPersonNumber }}
                      </td>
                    </tr>
                    <tr>
                      <td>責任者社員名（社内主担当者）</td>
                      <td class="text-left">
                        {{ formData.jobRelated.mainPerson }}
                      </td>
                    </tr>
                    <tr>
                      <td>使用Access拡張子</td>
                      <td class="text-left">
                        {{ formData.jobRelated.accessExtension }}
                      </td>
                    </tr>
                    <tr>
                      <td>サブ担当１</td>
                      <td class="text-left">
                        {{ formData.jobRelated.subPerson1 }}
                      </td>
                    </tr>
                    <tr>
                      <td>サブ担当２</td>
                      <td class="text-left">
                        {{ formData.jobRelated.subPerson2 }}
                      </td>
                    </tr>
                    <tr>
                      <td>サブ担当３</td>
                      <td class="text-left">
                        {{ formData.jobRelated.subPerson3 }}
                      </td>
                    </tr>
                    <tr>
                      <td>備考１</td>
                      <td class="text-left">
                        <span style="white-space: pre-line">{{ formData.jobRelated.remark1 }}</span>
                      </td>
                    </tr>
                    <tr>
                      <td>備考２</td>
                      <td class="text-left">
                        <span style="white-space: pre-line">{{ formData.jobRelated.remark2 }}</span>
                      </td>
                    </tr>
                    <tr>
                        <td>最終更新者</td>
                        <td class="text-left">
                          {{ formData.updateUser }}
                        </td>
                      </tr>
                      <tr>
                        <td>最終更新日付</td>
                        <td class="text-left">
                          {{ formData.updatedAt }}
                        </td>
                      </tr>
                  </table>
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

import {RPAApi} from "@/constants/api-url";
import RequestUtil from "@/utils/request-util";
import FnCommon from "@/utils/fn-common";
import Modal from "@/components/molecules/Modal";
import {RPAMessage} from "@/constants/message";

export default {
  name: "BusinessMngDetail",
  components: { Modal },
  data() {
    return {
      memo: {
        difficulties: [],
        manuals: [],
        formats: [],
        ledger: [],
      },
      formData: {
        id: null,
        status: null,
        difficultyName: null,
        manualName: null,
        mainPersonNumber: null,
        jobRisk:{
          attachment1: null, attachment2: null, manualFilePath1: null, manualFilePath2: null,
          riskLevelJob: null, amountCategoryName: null, paymentMethodName: null, dataCreationName: null
        },
        jobRelated: {
          attachment1: null, attachment2: null, difficulty: null, manual: null, format: null, personalized: null,
          provideFrom: null, provideTo: null, provideMethod: null, provideToDetail: null,
          mainPerson: null, accessExtension: null,
          subPerson1: null, subPerson2: null, subPerson3: null,
          remark1: null, remark2: null,
          manualFilePath: null,
        },
        department: null,
        departmentNumber: null,
        number: null,
        ledgerId: null,
        rpaNumber: null,
        category: null,
        name: null,
        description: null,
        personNumber: null,
        jobRpaInfo: {
          personName: null, consignFlag: null, consignee: null
        },
        jobAnalyzeTime: {
          workingTime: null, workingDays: null, workingMonths: null, yearlyTime: null, jobInterval: null, beforeKaizenTime: null, sakugenKosu: null, lastKaizenDate: null
        },
        ledgersLink: {
          updatedAt: null, ledgerId: null, is_deleted: null, id: null, createdAt: null
        },
        fileName: null,
        fileName2: null,
        fileName3: null,
        fileName4: null,
        updateUser: null,
        updatedAt: null
      },
      showModal: false,
      roleUser: null,
      currentFilePath: '',
    }
  },
  created(){

        RequestUtil.get(RPAApi.GET_DETAIL_USER).then((response) => {
            if (response.status === 200) {
              this.roleUser = response?.data?.mrole?.id;
            } else if(response.status === 404){
              return this.$router.push('login');
            }
          })
  },
  mounted() {
    Object.keys(this.memo).forEach(v => {
      RequestUtil.get(RPAApi.MEMO(v)).then(res => {
        if (res.status === 200) {
          this.memo[v] = res.data.body
        }
        
      })
    });
  },
  beforeMount() {
    this.sleep(1000).then(() => {
      RequestUtil.get(RPAApi.DETAIL_JOBS(this.$route.params.id)).then(res => {
        var res = res.data.body;
        FnCommon.copyProperties(this.formData, res);
        Object.assign(this.formData, res);
        this.formData.status = res?.mjobStatuse?.name;
        this.formData.department = res?.mdepartment?.name;
        this.formData.departmentNumber = res?.mdepartment?.number;
        this.formData.personNumber = res?.jobRpaInfo?.mperson?.personNumber;
        this.formData.jobRpaInfo.personName = res?.jobRpaInfo?.mperson?.personName;
        this.formData.jobRpaInfo.consignFlag = res?.jobRpaInfo?.mconsign?.name;
        this.formData.jobAnalyzeTime.jobInterval = res?.jobAnalyzeTime?.mjobInterval?.name;
        if (res?.jobAnalyzeTime?.workingTime) {
          this.formData.jobAnalyzeTime.workingTime = Number(res.jobAnalyzeTime.workingTime / 60).toFixed(2);
        }
        if (res?.jobAnalyzeTime?.workingDays) {
          this.formData.jobAnalyzeTime.workingDays = Number(res.jobAnalyzeTime.workingDays / 24).toFixed(2);
        }
        // if (res?.jobAnalyzeTime?.yearlyTime) {
        //   this.formData.jobAnalyzeTime.yearlyTime = Number(res.jobAnalyzeTime.yearlyTime / 60).toFixed(2);
        // }
        this.formData.jobAnalyzeTime.yearlyTime = Number(this.formData.jobAnalyzeTime.workingDays) * Number(this.formData.jobAnalyzeTime.workingTime) * this.formData.jobAnalyzeTime.workingMonths;
        if (res?.jobAnalyzeTime?.beforeKaizenTime) {
          this.formData.jobAnalyzeTime.beforeKaizenTime = Number(res.jobAnalyzeTime.beforeKaizenTime / 60).toFixed(2);
        }
        // if (res?.jobAnalyzeTime?.sakugenKosu) {
        //   this.formData.jobAnalyzeTime.sakugenKosu = Number(res.jobAnalyzeTime.sakugenKosu / 60).toFixed(2);
        // }
        this.formData.jobAnalyzeTime.sakugenKosu = Number(this.formData.jobAnalyzeTime.beforeKaizenTime - (this.formData.jobAnalyzeTime.yearlyTime)).toFixed(2);
        this.formData.jobAnalyzeTime.lastKaizenDate = res?.jobAnalyzeTime?.lastKaizenDate;
        this.formData.difficultyName = this.memo?.difficulties?.find(e => e.id == this.formData.jobRelated?.difficulty)?.name;
        this.formData.manualName = this.memo?.manuals?.find(e => e.id == this.formData.jobRelated?.manual)?.name;
        this.formData.jobRelated.format = this.memo?.formats?.[this.formData.jobRelated?.format];
        this.formData.jobRelated.personalized = res?.jobRelated?.mpersonalized?.name;
        this.formData.jobRelated.provideFrom = res?.jobRelated?.mprovideFrom?.name;
        this.formData.jobRelated.provideTo = res?.jobRelated?.mprovideTo?.name;
        this.formData.jobRelated.provideMethod = res?.jobRelated?.mprovideMethod?.name;
        this.formData.jobRelated.provideToDetail = res?.jobRelated?.mprovideToDetail?.name;
        this.formData.jobRelated.accessExtension = res?.jobRelated?.accessExtensionAsStr;
        this.formData.jobRelated.mainPerson = res?.jobRelated?.mperson?.personName;
        this.formData.mainPersonNumber = res?.jobRelated?.mperson?.personNumber;
        this.formData.jobRelated.subPerson1 = res?.jobRelated?.subPerson1?.personName;
        this.formData.jobRelated.subPerson2 = res?.jobRelated?.subPerson2?.personName;
        this.formData.jobRelated.subPerson3 = res?.jobRelated?.subPerson3?.personName;
        this.formData.updateUser = res?.updatedUser;
        if (!res?.jobRisk) {
          res.jobRisk = {
            riskLevelJob: null,
            attachment1: null,
            attachment2: null,
            mriskAmountCategory: { amountCategoryName: null },
            mriskDataCreation: { dataCreationName: null },
            mriskPaymentMethod: { paymentMethodName: null }
          };
        }
        if (!this.formData.jobRisk) {
          this.formData.jobRisk = {};
        }
        this.formData.jobRisk.riskLevelJob = res?.jobRisk?.riskLevelJob;
        this.formData.jobRisk.attachment1 = res?.jobRisk?.attachment1;
        this.formData.jobRisk.attachment2 = res?.jobRisk?.attachment2;
        this.formData.jobRisk.amountCategoryName= res?.jobRisk?.mriskAmountCategory?.amountCategoryName;
        this.formData.jobRisk.dataCreationName= res?.jobRisk?.mriskDataCreation?.dataCreationName;
        this.formData.jobRisk.paymentMethodName= res?.jobRisk?.mriskPaymentMethod?.paymentMethodName;
        var date = new Date(res?.updatedAt)
        this.formData.updatedAt = date.toLocaleString('en-ZA',  { hour12: false }).replace(",", "").replaceAll("/", "-");
        if (res?.jobRelated?.attachment1) {
          this.formData.fileName = res?.jobRelated?.attachment1.split('/').pop();
        }
        if (res?.jobRelated?.attachment2) {
          this.formData.fileName2 = res?.jobRelated?.attachment2.split('/').pop();

        }
        if (res?.jobRisk?.attachment1) {
          this.formData.fileName3 = res?.jobRisk?.attachment1.split('/').pop();
        }
        if (res?.jobRisk?.attachment2) {
          this.formData.fileName4 = res?.jobRisk?.attachment2.split('/').pop();

        }

        if (res?.ledgersLink?.id) {
          RequestUtil.get(RPAApi.DETAIL_LEDGERS_BY_LEDGER_LINK(res?.ledgersLink.id)).then(resLedger => {
            if (resLedger.status === 200) {
              this.formData.rpaNumber = resLedger.data.body.rpaNumber;
            }
          });
        }

        if(res.mdepartment == null && this.roleUser == 1){
          document.getElementById("btnSubmit").style.display ="unset";
        } else {
          RequestUtil.get(RPAApi.CHECK_DEPARTMENT(res.mdepartment.id)).then(resDept => {
            if(resDept.status === 200) {
              if(resDept.data.body == 3) {
                document.getElementById("btnSubmit").remove();
              }
              else {
                document.getElementById("btnSubmit").style.display ="unset";
              }
            }
          })
        }
      });
    });
  },
  methods: {
    backToList() {
      this.$router.push({name: 'business-management', params: {backFromDetail: true}});
    },
    onSubmit() {
      this.$router.push({name: 'business-management-edit', params: {id: this.formData.id}});
    },
    sleep(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    onDownload(fileName, type) {
      var fileURL = process.env.VUE_APP_API_URL + 'api/file/' + this.formData.id + '/' + type;
      var fileLink = document.createElement('a');
      fileLink.href = fileURL;
      fileLink.setAttribute('download', fileName);
      fileLink.setAttribute('target', '_blank');
      fileLink.click();
    },
    hideModal() {
      this.showModal = false;
    },
    openModal(filePath) {
      this.currentFilePath = filePath;
      this.showModal = true;
    },
    copy(filePath) {
      let textArea = document.createElement("textarea");
      textArea.value = filePath;
      document.body.appendChild(textArea);
      textArea.select();

      return new Promise((resolve, reject) => {
        document.execCommand('copy') ? resolve() : reject();
        textArea.remove();
      });
    },
    gotoFormDetailModal(filePath) {
      this.currentFilePath = filePath;
      this.showModal = true;
    },
  },
}
</script>

<style scoped>
.content-data {
  padding: 2px 16px;
}
</style>