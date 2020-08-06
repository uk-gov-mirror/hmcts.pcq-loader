provider "azurerm" {
  alias           = "mgmt"
  subscription_id = "${var.mgmt_subscription_id}"
  version         = "=1.33.1"
}

locals {
  vault_name                = "${var.product}-${var.env}"
  resource_group_name       = "${var.product}-${var.env}"
  storage_account_name      = "${var.product}shared${var.env}"
  mgmt_network_name         = "core-cftptl-intsvc-vnet"
  mgmt_network_rg_name      = "aks-infra-cftptl-intsvc-rg"
}

data "azurerm_subnet" "jenkins_subnet" {
  provider             = "azurerm.mgmt"
  name                 = "iaas"
  virtual_network_name = "${local.mgmt_network_name}"
  resource_group_name  = "${local.mgmt_network_rg_name}"
}

// pcq blob Storage Account
module "pcq_storage_account" {
  source                    = "git@github.com:hmcts/cnp-module-storage-account?ref=master"
  env                       = "${var.env}"
  storage_account_name      = "${local.storage_account_name}"
  resource_group_name       = "${local.resource_group_name}"
  location                  = "${var.location}"
  account_kind              = "StorageV2"
  account_tier              = "Standard"
  account_replication_type  = "LRS"
  access_tier               = "Hot"

  enable_blob_encryption    = true
  enable_file_encryption    = true
  enable_https_traffic_only = true

  // Tags
  common_tags               = "${var.common_tags}"
  team_contact              = "${var.team_contact}"
  destroy_me                = "${var.destroy_me}"

  network_rules {
    virtual_network_subnet_ids = ["${data.azurerm_subnet.jenkins_subnet.id}"]
    bypass                     = ["Logging", "Metrics", "AzureServices"]
    default_action             = "Deny"
  }
}

data "azurerm_key_vault" "key_vault" {
  name                = "${local.vault_name}"
  resource_group_name = "${local.vault_name}"
}

resource "azurerm_storage_container" "pcq_containers" {
  name                  = "pcq"
  storage_account_name  = "${module.pcq_storage_account.storageaccount_name}"
  container_access_type = "private"
}

// pcq blob Storage Account Vault Secrets
resource "azurerm_key_vault_secret" "pcq_storageaccount_id" {
  name      = "pcq-storage-account-id"
  value     = "${module.pcq_storage_account.storageaccount_id}"
  key_vault_id = "${data.azurerm_key_vault.key_vault.id}"
}

resource "azurerm_key_vault_secret" "pcq_storageaccount_primary_access_key" {
  name      = "pcq-storage-account-primary-access-key"
  value     = "${module.pcq_storage_account.storageaccount_primary_access_key}"
  key_vault_id = "${data.azurerm_key_vault.key_vault.id}"
}

resource "azurerm_key_vault_secret" "pcq_storageaccount_secondary_access_key" {
  name      = "pcq-storage-account-secondary-access-key"
  value     = "${module.pcq_storage_account.storageaccount_secondary_access_key}"
  key_vault_id = "${data.azurerm_key_vault.key_vault.id}"
}

resource "azurerm_key_vault_secret" "pcq_storageaccount_primary_connection_string" {
  name      = "pcq-storage-account-primary-connection-string"
  value     = "${module.pcq_storage_account.storageaccount_primary_connection_string}"
  key_vault_id = "${data.azurerm_key_vault.key_vault.id}"
}

resource "azurerm_key_vault_secret" "pcq_storageaccount_secondary_connection_string" {
  name      = "pcq-storage-account-secondary-connection-string"
  value     = "${module.pcq_storage_account.storageaccount_secondary_connection_string}"
  key_vault_id = "${data.azurerm_key_vault.key_vault.id}"
}

