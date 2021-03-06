package it.codingjam.github.util

import it.codingjam.github.vo.*
import java.util.*

interface TestData {
    companion object {
        val OWNER = Owner("login", "url")
        val REPO_1 = Repo(1, "name", "fullName", "desc", OWNER, 10000)
        val REPO_2 = Repo(2, "name", "fullName", "desc", OWNER, 10000)
        val REPO_3 = Repo(3, "name", "fullName", "desc", OWNER, 10000)
        val REPO_4 = Repo(4, "name", "fullName", "desc", OWNER, 10000)
        val CONTRIBUTOR1 = Contributor("login1", 10, "url1")
        val CONTRIBUTOR2 = Contributor("login2", 20, "url2")
        val REPO_DETAIL = RepoDetail(REPO_1, Arrays.asList(CONTRIBUTOR1, CONTRIBUTOR2))
        val USER = User("login", "avatar", "name", "company", "repos", "blog")
    }
}
