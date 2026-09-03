-- Create users table
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     email VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     first_name VARCHAR(100) NOT NULL,
                                     last_name VARCHAR(100) NOT NULL,
                                     role VARCHAR(50) NOT NULL,
                                     enabled BOOLEAN DEFAULT TRUE,
                                     email_verified BOOLEAN DEFAULT FALSE,
                                     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                     version BIGINT DEFAULT 0
);

-- Create projects table
CREATE TABLE IF NOT EXISTS projects (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        name VARCHAR(100) NOT NULL,
                                        description VARCHAR(500),
                                        status VARCHAR(50) NOT NULL,
                                        created_by UUID NOT NULL REFERENCES users(id),
                                        start_date TIMESTAMP WITH TIME ZONE,
                                        end_date TIMESTAMP WITH TIME ZONE,
                                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                        version BIGINT DEFAULT 0
);

-- Create tasks table
CREATE TABLE IF NOT EXISTS tasks (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     title VARCHAR(200) NOT NULL,
                                     description VARCHAR(2000),
                                     status VARCHAR(50) NOT NULL,
                                     priority VARCHAR(50) NOT NULL,
                                     due_date TIMESTAMP WITH TIME ZONE,
                                     estimated_hours DECIMAL(10,2),
                                     actual_hours DECIMAL(10,2),
                                     assignee_id UUID REFERENCES users(id),
                                     project_id UUID REFERENCES projects(id),
                                     created_by UUID NOT NULL REFERENCES users(id),
                                     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                     version BIGINT DEFAULT 0
);

-- Create comments table
CREATE TABLE IF NOT EXISTS comments (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        content VARCHAR(2000) NOT NULL,
                                        task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
                                        author_id UUID NOT NULL REFERENCES users(id),
                                        parent_comment_id UUID,
                                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create task_tags table
CREATE TABLE IF NOT EXISTS task_tags (
                                         task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
                                         tag VARCHAR(50) NOT NULL,
                                         PRIMARY KEY (task_id, tag)
);

-- Create project_members table
CREATE TABLE IF NOT EXISTS project_members (
                                               project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                                               user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                               PRIMARY KEY (project_id, user_id)
);

-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications (
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             user_id UUID NOT NULL,
                                             title VARCHAR(255) NOT NULL,
                                             content VARCHAR(2000),
                                             type VARCHAR(50) NOT NULL,
                                             is_read BOOLEAN DEFAULT FALSE,
                                             link VARCHAR(500),
                                             created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                             read_at TIMESTAMP WITH TIME ZONE
);

-- Create indexes
CREATE INDEX idx_tasks_assignee_id ON tasks(assignee_id);
CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_comments_task_id ON comments(task_id);
CREATE INDEX idx_comments_author_id ON comments(author_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(is_read);
CREATE INDEX idx_users_email ON users(email);

-- Create update trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_projects_updated_at BEFORE UPDATE ON projects FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_tasks_updated_at BEFORE UPDATE ON tasks FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert default admin user (password: admin123)
INSERT INTO users (id, email, password, first_name, last_name, role, enabled, email_verified)
VALUES (
           gen_random_uuid(),
           'admin@taskmanager.com',
           '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs',
           'Admin',
           'TaskManager',
           'ADMIN',
           true,
           true
       ) ON CONFLICT (email) DO NOTHING;