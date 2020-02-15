package org.jenkinsci.plugins.github.pullrequest.restrictions.GitHubPRUserRestriction

import lib.FormTagLib;

def f = namespace(FormTagLib);

f.entry(title: _("Organizations"), field: "orgs") {
    f.textarea()
}

f.entry(title: _("Users"), field: "users") {
    f.textarea()
}

//f.entry(title: _("Whitelist user msg"), field: "whitelistUserMsg") {
//    f.textbox(default: ".*add\\W+to\\W+whitelist.*")
//}

//f.entry(title: _("Organisations members as admins"),
//        field: "allowMembersOfWhitelistedOrgsAsAdmin") {
//    f.checkbox()
//}
