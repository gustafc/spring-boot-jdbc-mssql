package com.academicwork.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class SqlServerBlogrepository implements BlogRepository {

    @Autowired
    private DataSource dataSource;

    @Override
    public List<Blog> listBlogs() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, title FROM [BlogEngine].[Blogs]")) {
            List<Blog> blogs = new ArrayList<>();
            while (rs.next()) blogs.add(rsBlog(rs));
            return blogs;
        } catch (SQLException e) {
            throw new BlogRepositoryException(e);
        }
    }

    @Override
    public Blog getBlog(long blogId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, title FROM [BlogEngine].[Blogs] WHERE id = ?")) {
            ps.setLong(1, blogId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new BlogRepositoryException("No blog with ID " + blogId);
                else return rsBlog(rs);
            }
        } catch (SQLException e) {
            throw new BlogRepositoryException(e);
        }
    }

    @Override
    public User getAuthorOf(Blog blog) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT u.[UserID], u.[UserName], u.[FirstName], u.[LastName], u.[Email] " +
                     "FROM [Login].[Users] u JOIN [BlogEngine].[Blogs] b ON b.User_Id = u.UserID " +
                     "WHERE b.id = ?")) {
            ps.setLong(1, blog.id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new BlogRepositoryException("No blog with ID " + blog.id);
                else return new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
            }
        } catch (SQLException e) {
            throw new BlogRepositoryException(e);
        }
    }

    @Override
    public List<BlogPost> getEntriesIn(Blog blog) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT p.Id, p.Title, p.Body, p.EntryDate, p.Blog_Id " +
                     "FROM [BlogEngine].[Posts] p WHERE P.Blog_Id = ? ORDER BY p.EntryDate DESC")) {
            ps.setLong(1, blog.id);
            try (ResultSet rs = ps.executeQuery()) {
                List<BlogPost> posts = new ArrayList<>();
                while (rs.next()) posts.add(rsPost(rs));
                return posts;
            }
        } catch (SQLException e) {
            throw new BlogRepositoryException(e);
        }
    }

    private BlogPost rsPost(ResultSet rs) throws SQLException {
        return new BlogPost(
                rs.getLong("Id"),
                rs.getString("Title"),
                rs.getString("Body"),
                rs.getTimestamp("EntryDate").toLocalDateTime(),
                rs.getLong("Blog_Id")
        );
    }

    private Blog rsBlog(ResultSet rs) throws SQLException {
        return new Blog(rs.getLong("id"), rs.getString("title"));
    }
}
