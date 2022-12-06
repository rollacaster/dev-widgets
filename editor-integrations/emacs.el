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
                             " --position '"
                             "\["
                             (number-to-string (line-number-at-pos))
                             " "
                             (number-to-string (current-column))
                             "\]' --start-pos '"
                             "\["
                             (number-to-string (first position))
                             " "
                             (number-to-string (rest position))
                             "\]'")))
    (let ((pid (number-to-string (apply #'min (mapcar #'string-to-number (split-string (string-trim (shell-command-to-string "ps aux | grep cljfx | awk '/java/{print $2}'")) "\n"))))))
      (shell-command (concat "osascript -e 'tell application \"System Events\"
    set frontmost of every process whose unix id is " pid " to true
end tell'")))))
