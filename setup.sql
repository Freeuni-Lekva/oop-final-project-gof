-- USE YOUR DATABASE --
use enter_your_database_name;

-- DROP UNNECESSARY TABLES --
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS followers;
DROP TABLE IF EXISTS stories;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS bookmarks;
DROP TABLE IF EXISTS read_history;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS story_tags;
DROP TABLE IF EXISTS chats;
DROP TABLE IF EXISTS messages;

-- CREATE TABLES ---
CREATE TABLE users (
        user_id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50) NOT NULL UNIQUE,
        password_hash VARCHAR(256) NOT NULL,
        age INT,
        register_time DATETIME DEFAULT CURRENT_TIMESTAMP,
        active BOOLEAN DEFAULT TRUE,
        last_login DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        is_creator BOOLEAN DEFAULT FALSE
);

CREATE TABLE followers (
        follower_id INT,
        following_id INT,
        PRIMARY KEY (follower_id, following_id),
        FOREIGN KEY (follower_id) REFERENCES users(user_id) ON DELETE CASCADE,
        FOREIGN KEY (following_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE stories (
        story_id INT AUTO_INCREMENT PRIMARY KEY,
        creator_id INT NOT NULL,
        title VARCHAR(200) NOT NULL,
        prompt TEXT,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (creator_id) REFERENCES users(user_id)
);

CREATE TABLE posts (
        post_id INT AUTO_INCREMENT PRIMARY KEY,
        story_id INT NOT NULL,
        image_url VARCHAR(256),
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        like_count INT DEFAULT 0,
        comment_count INT DEFAULT 0,
        FOREIGN KEY (story_id) REFERENCES stories(story_id) ON DELETE CASCADE
);

CREATE TABLE comments (
        comment_id INT AUTO_INCREMENT PRIMARY KEY,
        author_id INT NOT NULL,
        post_id INT NOT NULL,
        comment TEXT NOT NULL,
        FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE,
        FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);

CREATE TABLE likes (
        like_id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        post_id INT,
        comment_id INT,
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
        FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE SET NULL,
        FOREIGN KEY (comment_id) REFERENCES comments(comment_id) ON DELETE SET NULL,

        CHECK (
            (post_id IS NOT NULL AND comment_id IS NULL) OR
            (post_id IS NULL AND comment_id IS NOT NULL)
        )
);

CREATE TABLE bookmarks (
        user_id INT,
        story_id INT,
        PRIMARY KEY (user_id, story_id),
        FOREIGN KEY (user_id) REFERENCES users(user_id),
        FOREIGN KEY (story_id) REFERENCES stories(story_id) ON DELETE CASCADE
);

CREATE TABLE read_history (
        user_id INT,
        story_id INT,
        last_read_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        PRIMARY KEY (user_id, story_id),
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
        FOREIGN KEY (story_id) REFERENCES stories(story_id) ON DELETE CASCADE
);

CREATE TABLE tags (
        tag_id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE story_tags (
      story_id INT,
      tag_id INT,
      PRIMARY KEY (story_id, tag_id),
      FOREIGN KEY (story_id) REFERENCES stories(story_id) ON DELETE CASCADE,
      FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
);

CREATE TABLE chats (
      chat_id INT AUTO_INCREMENT PRIMARY KEY,
      story_id INT NOT NULL,
      user_id INT NOT NULL,
      FOREIGN KEY (story_id) REFERENCES stories(story_id) ON DELETE CASCADE,
      FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE messages (
      message_id INT AUTO_INCREMENT PRIMARY KEY,
      message TEXT NOT NULL,
      chat_id INT NOT NULL,
      is_user BOOLEAN DEFAULT TRUE,
      FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE
);


-- INSERT INTO TABLES --

-- Insert users
INSERT INTO users (username, password_hash, age, is_creator, active)
VALUES
    ('creator_john', 'hash_john_123', 35, TRUE, TRUE),
    ('user_jane', 'hash_jane_456', 28, FALSE, TRUE),
    ('creator_alex', 'hash_alex_789', 40, TRUE, FALSE),
    ('user_emily', 'hash_emily_101', 22, FALSE, TRUE);

-- Insert stories for creators
INSERT INTO stories (creator_id, title, prompt)
VALUES
    (1, 'The Mystery of the Lost Artifact', 'A suspenseful journey to uncover hidden secrets.'),
    (3, 'Adventures in the Digital Age', 'Exploring the vast world of cyberspace.');

-- Insert posts for stories
INSERT INTO posts (story_id, image_url, like_count, comment_count)
VALUES
    (1, 'http://example.com/artifact1.jpg', 120, 15),
    (2, 'http://example.com/digitalage1.jpg', 85, 20);

-- Insert comments from users
INSERT INTO comments (author_id, post_id, comment)
VALUES
    (2, 1, 'Amazing post!'),
    (4, 2, 'Great insights on the topic!');

-- Insert tags and associate them with stories
INSERT INTO tags (name)
VALUES
    ('Adventure'),
    ('Mystery'),
    ('Technology');
INSERT INTO story_tags (story_id, tag_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 3);

-- Insert chats and messages
INSERT INTO chats (story_id, user_id)
VALUES
    (1, 2),
    (2, 4);

INSERT INTO messages (chat_id, message, is_user)
VALUES
    (1, 'This story is amazing!', TRUE),
    (2, 'I have some questions about the plot.', TRUE);
