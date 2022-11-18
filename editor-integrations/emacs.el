(defun dev-widget (&optional additional-params)
  (interactive)
  (when-let ((filename (buffer-file-name)))
    (save-buffer))
  (let ((default-directory "/Users/thomas/projects/dev-widgets"))
    (shell-command (let ((position (window-absolute-pixel-position)))
                     (concat "bb run "
                             "--path '"
                             buffer-file-name
                             "'"
                             " --position "
                             (number-to-string (point))
                             " --start-pos '"
                             "\["
                             (number-to-string (first position))
                             " "
                             (number-to-string (rest position))
                             "\]'")))))
