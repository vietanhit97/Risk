export const RPAApi = {
  MSYNCHROID_VERSION: 'api/memo/synchroidVersion',
  MDEPARTMENT_LIST: 'api/memo/departments',
  MSTATUS_LIST: 'api/memo/status',
  SCHEDULE_OPTIONS: 'api/memo/schedule',
  EXEC_TYPES: 'api/memo/execTypes',
  EXEC_DAYS: 'api/memo/execDays',
  LIST_MINTERVAL: 'api/memo/intervals',
  LIST_PROGRESS: 'api/memo/progress',
  LIST_PROGRESS_CHANGE_FLG: 'api/memo/progressChangeFlg',
  LIST_ENHANCE_FLG: 'api/memo/enhanceFlg',
  LIST_AUTO_FLG: 'api/memo/automationFlg',
  LIST_JOB_INTERVAL: 'api/memo/jobInterval',
  LIST_JOB_DIFF: 'api/memo/jobDiffType',
  LIST_JOB_MANUAL: 'api/memo/jobManual',
  LIST_ACCESS_EXT: 'api/memo/accessExtension',
  LIST_HEARING_FLG: 'api/memo/hearingFlg',
  LIST_RPA_PRIORITY: 'api/memo/rpaPriority',
  LIST_ACCESS_EXCEL: 'api/memo/accessExcel',
  LIST_USER_REVIEW: 'api/memo/userReview',
  LIST_MCOMMON: 'api/memo/common',
  LIST_MPROVIDE_FROM: 'api/memo/provideFrom',
  LIST_MPROVIDE_TO: 'api/memo/provideTo',
  LIST_MCATEGORY: 'api/memo/categories',
  LIST_LEDGER: 'api/memo/ledger',
  LIST_AMOUNTCATEGORIES: 'api/memo/amount_categories',
  LIST_DATACREATIONS: 'api/memo/data_creations',
  LIST_PAYMENTMETHOD: 'api/memo/payment_methods',
  // JOB_NUMBER: 'api/memo/jobnumber',
  EXIST_RPANUMBER: (rpaNumber) => {
    return 'api/memo/checkRPANo/' + rpaNumber
  },
  JOB_DEPARTMENT: (ledgerId) => {
    return 'api/memo/jobDepartment/' + ledgerId;
  },

  MEMO: (key) => {
    return `api/memo/${key}`
  },
  /*job*/
  BUSSINESS: 'api/jobs',
  BUSSINESS_TOTAL: 'api/jobs/total',
  DETAIL_JOBS: (id) => {
    return 'api/jobs/' + id;
  },
  DETAIL_JOB_BY_LEDGER_LINK: (ledger_link_id) => {
    return 'api/jobs/get_by_ledger_link/' + ledger_link_id;
  },
  CREATE_JOBS: 'api/jobs',
  DELETE_JOBS: (id) => {
    return 'api/jobs/' + id;
  },
  CHECK_NUMBER: (number) => {
    return 'api/jobs/checkNumber/' + number
  },
  /*robot*/
  LIST_LEDGERS: 'api/ledgers',
  SCHEDULES: 'api/schedules',
  SCHEDULES_MORE: 'api/schedules/more',
  HOLIDAYS: 'api/holidays',
  LOGIN: 'api/login',
  XML_LAST_SYNC_TIME: 'api/schedules/latest_sync',
  CHANGE_PASSWORD: 'api/change-password',
  UPLOAD_FILE: (folder) => {
    return 'api/file/upload/' + folder
  },
  DELETE_FILE: (folder, fileName, updateDB) => {
    return 'api/file/' + folder + '/' + fileName + '/' + updateDB
  },
  GET_FILE: (filePath) => {
    return 'api/file/' + filePath
  },
  DETAIL_RPA_SPEC: (id) => {
    return 'api/rpa-spec/' + id
  },
  DETAIL_HOLIDAYS: (id) => {
    return 'api/holidays/' + id;
  },
  CREATE_LEDGERS: 'api/ledgers',
  DELETE_LEDGER: (id) => {
    return 'api/ledgers/' + id;
  },
  DETAIL_LEDGERS: (id) => {
    return 'api/ledgers/' + id;
  },
  DETAIL_LEDGERS_BY_LEDGER_LINK: (ledger_link_id) => {
    return 'api/ledgers/get_by_ledger_link/' + ledger_link_id;
  },
  CREATE_SCHEDULE: 'api/ledgers/schedule',
  GET_SCHEDULE: 'api/ledgers/schedule',

  /*user*/
  USER_LIST : 'api/persons',
  DETAIL_USER: (id) => {
    return 'api/persons/' + id;
  },
  CREATE_USER: 'api/persons',

  EXIST_PERSONNUMBER: (personNumber) => {
    return 'api/persons/checkPersonNumber/' + personNumber
  },

  SEND_MAIL: (id) => {
    return 'api/persons/checkSendMail/' + id;
  }, 

  GET_ROLE: 'api/getRoles',

  CHECK_DEPARTMENT: (deptId) => {
    return 'api/jobs/checkDepartment/' + deptId;
  },

  FORGOT_PASSWORD: 'api/forgotPassword',

  GET_DETAIL_USER: 'api/persons/getDetailUser',

}
