variable "product" {
  type = "string"
  default = "pcq"
}

variable "location" {
  default = "UK South"
}

variable "env" {
  type = "string"
}

// TAG SPECIFIC VARIABLES
variable "common_tags" {
  type = "map"
}

variable "team_contact" {
  type        = "string"
  description = "The name of your Slack channel people can use to contact your team about your infrastructure"
  default     = "#pcq-support"
}

variable "destroy_me" {
  type        = "string"
  description = "Here be dragons! In the future if this is set to Yes then automation will delete this resource on a schedule. Please set to No unless you know what you are doing"
  default     = "No"
}
