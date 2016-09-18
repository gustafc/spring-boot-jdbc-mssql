package com.academicwork.blog;

import java.util.List;

public interface BlogRepository {
    List<Blog> listBlogs();
    Blog getBlog(long blogId);
    User getAuthorOf(Blog blog);
    List<BlogPost> getEntriesIn(Blog blog);
}
